package crud.server.bank.db.search

import cats.data.Validated.Valid
import cats.data.{Validated, ValidatedNel}
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
import scala.util.Try
import cats.data.Validated
import cats.data.Validated._
import cats.implicits._


class ClientSearchService(db: PsqlDatabase = PsqlDatabaseImpl)
  extends TableSearchService[Client, ClientTable, ManyQueryForm, CreateQueryForm, UpdateQueryForm](db, tableQuery) {
  private def queryById(id: Int) = tableQuery.filter(_.id === id)

  override protected def getAction(id: Id): DBIOAction[Option[Client], NoStream, Nothing] =
    queryById(id).result.headOption

  override protected def deleteAction(id: Id): DBIOAction[Int, NoStream, Nothing] = queryById(id).delete

  override def validateGetManyQuery(m: ManyQueryForm): AllErrorsOr[GetManyDbQuery[Client, ClientTable]] = {
    def validatePagination: AllErrorsOr[(Int, Int)] =
      Validated
        .cond(m.page >= 0 && m.size >= 0, (m.page, m.size), "page and size should be non negative").toValidatedNel
    def validateSortField: AllErrorsOr[ClientSortField[_]] =
      m.sortBy match {
        case Some(sortBy) => ClientSortField.fromString(sortBy).toValidNel(s"Unknown sortBy type: $sortBy")
        case None => ClientSortField.Id.validNel
      }
    def validateSortType: AllErrorsOr[SortType] =
      SortType.fromString(m.sortType).toValidNel(s"Unknown sort type: ${m.sortType}")

    (validatePagination, validateSortField, validateSortType).mapN {
      case ((page, size), sortBy, sortType) => GetManyClientsDbQuery(size, page, m.adult, sortBy, sortType)
    }
  }

  override def validateCreateQuery(form: CreateQueryForm): AllErrorsOr[CreateDbQuery[Client, ClientTable]] =
    Valid(CreateClientDbQuery(form.name, form.surname, form.birthYear))

  override def validateUpdateQuery(form: UpdateQueryForm): AllErrorsOr[UpdateDbQuery[Client, ClientTable]] =
    Valid(UpdateClientDbQuery(form.id, form.money))
}
