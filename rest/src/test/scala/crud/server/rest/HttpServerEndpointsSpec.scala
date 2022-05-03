package crud.server.rest

import akka.http.scaladsl.model.{MediaType, StatusCodes}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import crud.server.api.db.PsqlDatabase
import crud.server.api.search.SearchService
import crud.server.bank.db.entity.Client
import crud.server.bank.db.search.{ClientSearchService, CreateQueryForm, ManyQueryForm, UpdateQueryForm}
import crud.server.bank.db.table.ClientTable
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import java.util.concurrent.TimeUnit
import scala.concurrent.Await
import scala.concurrent.duration.Duration

class HttpServerEndpointsSpec
  extends AnyWordSpec
  with Matchers
  with ScalatestRouteTest
  with RoutesDef[TestGetManyQuery, TestCreateQuery, TestUpdateQuery] {
  "The service" should {
    "return OK when GET /clients is valid" in {
      Get("/clients?size=2&page=1&adult=false&sortBy=name&sort=desc") ~> routes ~> check {
        status shouldBe StatusCodes.OK
        mediaType.isText shouldBe true
      }
    }

    "return NoContent deleting existing resource (id == 1) by DELETE /clients/:id" in {
      Delete("/clients/1") ~> routes ~> check {
        status shouldBe StatusCodes.NoContent
      }
    }

    "return CREATED when new user created by POST /clients" in {
      Post("/clients?name=Jan&surname=Nowak&birthYear=1990") ~> routes ~> check {
        status shouldBe StatusCodes.Created
      }
    }

    "return NotFound deleting non-existing (id == 1) resource by DELETE /clients/:id" in {
      Delete("/clients/1") ~> Route.seal(routes) ~> check {
        status shouldBe StatusCodes.NotFound
      }
    }

    "return NoContent updating existing resource by POST /clients/:id" in {
      Post("/clients/2?money=1") ~> routes ~> check {
        status shouldBe StatusCodes.NoContent
      }
    }

    "return NotFound updating non-existing (id == 1) resource by POST /clients/:id" in {
      Post("/clients/1?money=1") ~> Route.seal(routes) ~> check {
        status shouldBe StatusCodes.NotFound
      }
    }

    "return OK getting existing resource (id == 4) by GET /clients/:id" in {
      Get("/clients/4") ~> routes ~> check {
        status shouldBe StatusCodes.OK
      }
    }

    "return NotFound getting non-existing resource by GET /clients/:id" in {
      Get("/clients/1") ~> Route.seal(routes) ~> check {
        status shouldBe StatusCodes.NotFound
      }
    }
  }

  override protected val clientSearchService: SearchService[Client, TestGetManyQuery, TestCreateQuery, TestUpdateQuery, ManyQueryForm, CreateQueryForm, UpdateQueryForm] =
    TestMemorySearchService
}
