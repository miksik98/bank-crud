package crud.server.rest

import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.model.{HttpMethod, HttpMethods, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{Route, StandardRoute, ValidationRejection}
import cats.data.Validated
import crud.server.api.metrics.MetricsRegistry
import crud.server.api.query.{CreateQuery, GetManyQuery, UpdateQuery}
import crud.server.api.search.SearchService
import crud.server.bank.db.entity.Client
import crud.server.bank.db.search.{CreateQueryForm, ManyQueryForm, UpdateQueryForm}
import io.circe.Encoder
import io.circe.generic.semiauto._
import io.circe.syntax.EncoderOps
import io.prometheus.client.{Counter, Gauge}
import slick.util.Logging
import crud.server.rest.metrics.EndpointMetrics._
import io.prometheus.client.exporter.common.TextFormat


trait RoutesDef[M <: GetManyQuery, C <: CreateQuery, U <: UpdateQuery[Client]] extends Logging {
  protected val clientSearchService: SearchService[Client, M, C, U, ManyQueryForm, CreateQueryForm, UpdateQueryForm]

  implicit private val clientEncoder: Encoder[Client] = deriveEncoder

  private def route[T](v: Validated[String, T], onValid: T => Route): Route = {
    v match {
      case Validated.Valid(query) => onValid(query)
      case Validated.Invalid(e)   => reject(ValidationRejection(e))
    }
  }

  private def getMany(form: ManyQueryForm)(onValid: M => Route): Route =
    route(clientSearchService.validateGetManyQuery(form), onValid)

  private def create(form: CreateQueryForm)(onValid: C => Route): Route =
    route(clientSearchService.validateCreateQuery(form), onValid)

  private def update(form: UpdateQueryForm)(onValid: U => Route): Route =
    route(clientSearchService.validateUpdateQuery(form), onValid)

  private def richComplete(log: String, method: HttpMethod)(m: ToResponseMarshallable)(implicit c: Counter): StandardRoute = {
    logger.info(log)
    c.labels(method.value).inc()
    complete(m)
  }

  val routes: Route = {
    pathPrefix("clients") {
      pathEndOrSingleSlash {
        implicit val endpointCounter: Counter = clientsCounter
        get {
          parameters(
            "size".as[Int],
            "page".as[Int],
            "adult".as[Boolean].optional,
            "sortBy".optional,
            "sort".withDefault("asc")
          ).as(ManyQueryForm.apply) {
            getMany(_) { query =>
              onSuccess(clientSearchService.getMany(query)) { clients =>
                richComplete(s"Successful multi-get $query", HttpMethods.GET)(StatusCodes.OK -> clients.asJson.noSpaces)
              }
            }
          }
        } ~ post {
          parameters("name", "surname", "birthYear".as[Int]).as(CreateQueryForm.apply) {
            create(_) { query =>
              onSuccess(clientSearchService.create(query))(_ => richComplete(s"Successful creation of client $query", HttpMethods.POST)(StatusCodes.Created))
            }
          }
        }
      } ~ pathPrefix(Segment) { id =>
        implicit val endpointCounter: Counter = clientsIdCounter
        pathEndOrSingleSlash {
          get {
            onSuccess(clientSearchService.get(id.toInt)) {
              case Some(c) => richComplete(s"Successful get of client with id $id", HttpMethods.GET)(StatusCodes.OK -> c.asJson.noSpaces)
              case None    => reject
            }
          } ~ post {
            parameter("money".as[Double]) { money =>
              update(UpdateQueryForm(id.toInt, money)) { query =>
                onSuccess(clientSearchService.update(query)) { i =>
                  if (i == 0) reject
                  else richComplete(s"Successful update of client with id $id with money $id", HttpMethods.POST)(StatusCodes.NoContent)
                }
              }
            }
          } ~ delete {
            onSuccess(clientSearchService.delete(id.toInt)) { i =>
              if (i == 0) reject
              else richComplete(s"Successful deletion of client with id $id", HttpMethods.DELETE)(StatusCodes.NoContent)
            }
          }
        }
      }
    } ~ pathPrefix("metrics") {
      pathEndOrSingleSlash {
        get {
          complete(StatusCodes.OK -> MetricsRegistry.export)
        }
      }
    }
  }
}
