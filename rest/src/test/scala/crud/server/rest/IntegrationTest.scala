package crud.server.rest

import akka.http.scaladsl.model.{StatusCode, StatusCodes}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import crud.server.api.db.query.{CreateDbQuery, GetManyDbQuery, UpdateDbQuery}
import crud.server.api.search.SearchService
import crud.server.bank.db.entity.Client
import crud.server.bank.db.search.{ClientSearchService, CreateQueryForm, ManyQueryForm, UpdateQueryForm}
import crud.server.bank.db.table.ClientTable
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.Await
import scala.concurrent.duration.DurationInt

class IntegrationTest
  extends AnyWordSpec
  with Matchers
  with ScalatestRouteTest
  with RoutesDef[
    GetManyDbQuery[Client, ClientTable],
    CreateDbQuery[Client, ClientTable],
    UpdateDbQuery[Client, ClientTable]
  ] {


  "System" should {
    Await.result(TestDatabase.run(ClientTable.tableQuery.schema.create), 1.seconds)

    "Create 5 clients when using POST /clients" in {
      val forms = List(
        CreateQueryForm("Jan", "Nowak", 1980),
        CreateQueryForm("Bogdan", "Wilk", 1970),
        CreateQueryForm("Stefan", "Kowalski", 2010),
        CreateQueryForm("Patrycja", "Szara", 2000),
        CreateQueryForm("Jadwiga", "Nowak", 1990)
      )

      for (form <- forms) {
        Post(s"/clients?name=${form.name}&surname=${form.surname}&birthYear=${form.birthYear}") ~> routes ~> check {
          status shouldBe StatusCodes.Created
        }
      }

      for (id <- 1 to 5) {
        Get(s"/clients/$id") ~> routes ~> check {
          status shouldBe StatusCodes.OK
        }
      }

      Get(s"/clients/6") ~> Route.seal(routes) ~> check {
        status shouldBe StatusCodes.NotFound
      }
    }

    "Delete client when using DELETE /clients/id" in {
      Delete(s"/clients/1") ~> routes ~> check {
        status shouldBe StatusCodes.NoContent
      }

      Delete(s"/clients/1") ~> Route.seal(routes) ~> check {
        status shouldBe StatusCodes.NotFound
      }

      Get(s"/clients/1") ~> Route.seal(routes) ~> check {
        status shouldBe StatusCodes.NotFound
      }

      Post(s"/clients/1?money=10") ~> Route.seal(routes) ~> check {
        status shouldBe StatusCodes.NotFound
      }
    }

    "Update client when using POST /clients/id" in {
      Get(s"/clients/2") ~> routes ~> check {
        entityAs[String] shouldBe "{\"id\":2,\"name\":\"Bogdan\",\"surname\":\"Wilk\",\"birthYear\":1970,\"money\":0.00}"
      }

      Post(s"/clients/2?money=20") ~> routes ~> check {
        status shouldBe StatusCodes.NoContent
      }

      Get(s"/clients/2") ~> routes ~> check {
        entityAs[String] shouldBe "{\"id\":2,\"name\":\"Bogdan\",\"surname\":\"Wilk\",\"birthYear\":1970,\"money\":20.00}"
      }

      Post(s"/clients/2?money=-10") ~> routes ~> check {
        status shouldBe StatusCodes.NoContent
      }

      Get(s"/clients/2") ~> routes ~> check {
        entityAs[String] shouldBe "{\"id\":2,\"name\":\"Bogdan\",\"surname\":\"Wilk\",\"birthYear\":1970,\"money\":10.00}"
      }
    }

    "Filter correctly when using GET /clients" in {
      Get("/clients?page=0&size=5&adult=false") ~> routes ~> check {
        entityAs[String] shouldBe "[{\"id\":3,\"name\":\"Stefan\",\"surname\":\"Kowalski\",\"birthYear\":2010,\"money\":0.00}]"
      }
    }

    "Sort correctly with pagination when using GET /clients" in {
      Get("/clients?page=0&size=1&sort=desc") ~> routes ~> check {
        entityAs[String] shouldBe "[{\"id\":5,\"name\":\"Jadwiga\",\"surname\":\"Nowak\",\"birthYear\":1990,\"money\":0.00}]"
      }

      Get("/clients?page=1&size=1&sortBy=birthYear") ~> routes ~> check {
        entityAs[String] shouldBe "[{\"id\":5,\"name\":\"Jadwiga\",\"surname\":\"Nowak\",\"birthYear\":1990,\"money\":0.00}]"
      }

      Get("/clients?page=0&size=1&sortBy=money&sort=desc") ~> routes ~> check {
        entityAs[String] shouldBe "[{\"id\":2,\"name\":\"Bogdan\",\"surname\":\"Wilk\",\"birthYear\":1970,\"money\":10.00}]"
      }

      Get("/clients?page=3&size=1&sortBy=surname") ~> routes ~> check {
        entityAs[String] shouldBe "[{\"id\":2,\"name\":\"Bogdan\",\"surname\":\"Wilk\",\"birthYear\":1970,\"money\":10.00}]"
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
