package crud.server.rest

import cats.data.Validated
import cats.data.Validated.Valid
import crud.server.api.query.{CreateQuery, GetManyQuery, UpdateQuery}
import crud.server.api.search.SearchService
import crud.server.bank.db.entity.Client
import crud.server.bank.db.search.{CreateQueryForm, ManyQueryForm, UpdateQueryForm}

import java.time.LocalDate
import scala.concurrent.Future

case class TestGetManyQuery(
  adult: Option[Boolean]
) extends GetManyQuery

case class TestCreateQuery(name: String, surname: String, birthYear: Int) extends CreateQuery

case class TestUpdateQuery(id: Int, money: Double) extends UpdateQuery[Client]

object TestMemorySearchService
  extends SearchService[
    Client,
    TestGetManyQuery,
    TestCreateQuery,
    TestUpdateQuery,
    ManyQueryForm,
    CreateQueryForm,
    UpdateQueryForm
  ] {

  private var clients: Seq[Client] = Seq(
    Client(1, "Jan", "Kowalski", 2000, 300),
    Client(2, "Anna", "Żak", 1990, 500),
    Client(3, "Piotr", "Nowak", 2010, 200)
  )

  override def validateGetManyQuery(form: ManyQueryForm): AllErrorsOr[TestGetManyQuery] = Valid(TestGetManyQuery(form.adult))

  override def validateCreateQuery(form: CreateQueryForm): AllErrorsOr[TestCreateQuery] = Valid(TestCreateQuery(form.name, form.surname, form.birthYear))

  override def validateUpdateQuery(form: UpdateQueryForm): AllErrorsOr[TestUpdateQuery] = Valid(TestUpdateQuery(form.id, form.money))

  override def getMany(query: TestGetManyQuery): Future[Seq[Client]] = {
    val filtered = query.adult match {
      case Some(adult) => if (adult) clients.filter(_.birthYear * (-1) + LocalDate.now.getYear > 18) else clients.filter(_.birthYear * (-1) + LocalDate.now.getYear <= 18)
      case None => clients
    }
    Future.successful(filtered)
  }

  override def get(id: Id): Future[Option[Client]] = Future.successful(clients.find(_.id == id))

  override def create(query: TestCreateQuery): Future[Int] = {
    clients = clients :+ Client(clients.map(_.id).max + 1, query.name, query.surname, query.birthYear, 0)
    Future.successful(1)
  }

  override def delete(id: Id): Future[Int] = {
    clients.find(_.id == id) match {
      case Some(_) =>
        clients = clients.filterNot(_.id == id)
        Future.successful(1)
      case None => Future.successful(0)
    }
  }

  override def update(query: TestUpdateQuery): Future[Int] = {
    clients.find(_.id == query.id) match {
      case Some(client) =>
        clients = clients.filterNot(_.id == query.id) :+ client.copy(money = client.money + query.money)
        Future.successful(1)
      case None => Future.successful(0)
    }
  }
}
