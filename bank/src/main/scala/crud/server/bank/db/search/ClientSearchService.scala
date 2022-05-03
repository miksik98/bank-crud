package crud.server.bank.db.search

import cats.data.Validated
import cats.data.Validated.{Invalid, Valid}
import crud.server.api.db.PsqlDatabase
import crud.server.api.db.query.{CreateDbQuery, GetManyDbQuery, SortType, UpdateDbQuery}
import crud.server.api.db.search.TableSearchService
import crud.server.bank.db.PsqlDatabaseImpl
import crud.server.bank.db.entity.Client
import crud.server.bank.db.query.{ClientSortField, CreateClientDbQuery, GetManyClientsDbQuery, UpdateClientDbQuery}
import crud.server.bank.db.table.ClientTable
import crud.server.bank.db.table.ClientTable.tableQuery
import slick.dbio.{DBIOAction, NoStream}
import slick.jdbc.PostgresProfile.api._

class ClientSearchService(db: PsqlDatabase = PsqlDatabaseImpl)
  extends TableSearchService[Client, ClientTable, ManyQueryForm, CreateQueryForm, UpdateQueryForm](db, tableQuery) {
  private def queryById(id: Int) = tableQuery.filter(_.id === id)

  override protected def getAction(id: Id): DBIOAction[Option[Client], NoStream, Nothing] =
    queryById(id).result.headOption

  override protected def deleteAction(id: Id): DBIOAction[Int, NoStream, Nothing] = queryById(id).delete

  override def validateGetManyQuery(m: ManyQueryForm): Validated[String, GetManyDbQuery[Client, ClientTable]] = {
    if (m.page < 0 || m.size < 0) Invalid("page and size should be non negative")
    else
      (m.sortBy.map(ClientSortField.fromString), SortType.fromString(m.sortType)) match {
        case (Some(Invalid(cause)), _) => Invalid(cause)
        case (Some(Valid(sortBy)), Valid(sortType)) =>
          Valid(GetManyClientsDbQuery(m.size, m.page, m.adult, sortBy, sortType))
        case (None, Valid(sortType)) =>
          Valid(GetManyClientsDbQuery(m.size, m.page, m.adult, ClientSortField.Id, sortType))
        case (_, Invalid(cause)) => Invalid(cause)
      }
  }

  override def validateCreateQuery(form: CreateQueryForm): Validated[String, CreateDbQuery[Client, ClientTable]] =
    Valid(CreateClientDbQuery(form.name, form.surname, form.birthYear))

  override def validateUpdateQuery(form: UpdateQueryForm): Validated[String, UpdateDbQuery[Client, ClientTable]] =
    Valid(UpdateClientDbQuery(form.id, form.money))
}
