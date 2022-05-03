package crud.server.rest

import akka.http.scaladsl.server._
import akka.http.scaladsl.testkit.ScalatestRouteTest
import crud.server.api.db.query.{CreateDbQuery, GetManyDbQuery, UpdateDbQuery}
import crud.server.api.search.SearchService
import crud.server.bank.db.entity.Client
import crud.server.bank.db.search.{ClientSearchService, CreateQueryForm, ManyQueryForm, UpdateQueryForm}
import crud.server.bank.db.table.ClientTable
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class HttpServerValidationSpec
  extends AnyWordSpec
  with Matchers
  with ScalatestRouteTest
  with RoutesDef[GetManyDbQuery[Client, ClientTable], CreateDbQuery[Client, ClientTable], UpdateDbQuery[Client, ClientTable]] {

  "The service" should {

    "return missing param rejection while not providing page" in {
      Get("/clients?size=2") ~> routes ~> check {
        rejection shouldEqual MissingQueryParamRejection("page")
      }
    }

    "return missing param rejection while not providing size" in {
      Get("/clients?page=0") ~> routes ~> check {
        rejection shouldEqual MissingQueryParamRejection("size")
      }
    }

    "return malformed query param rejection while providing wrong data type of page" in {
      Get("/clients?size=2&page=a") ~> routes ~> check {
        rejection match {
          case MalformedQueryParamRejection("page", _, _) =>
          case _                                          => fail()
        }
      }
    }

    "return validation rejection while providing unknown sortBy" in {
      Get("/clients?size=2&page=1&sortBy=m") ~> routes ~> check {
        rejection shouldEqual ValidationRejection("Unknown sortBy type: m")
      }
    }

    "return validation rejection while providing unknown sort" in {
      Get("/clients?size=2&page=1&sortBy=name&sort=a") ~> routes ~> check {
        rejection shouldEqual ValidationRejection("Unknown sort type: a")
      }
    }
  }
  override protected val clientSearchService: SearchService[
    Client,
    GetManyDbQuery[Client, ClientTable],
    CreateDbQuery[Client, ClientTable],
    UpdateDbQuery[Client, ClientTable],
    ManyQueryForm,
    CreateQueryForm,
    UpdateQueryForm
  ] = new ClientSearchService(TestDatabase)
}
