package crud.server.bank.db.query

import crud.server.api.db.DbImplicits.ColumnOrderedSortType
import crud.server.api.db.query.{GetManyDbQuery, SortType}
import crud.server.bank.db.entity.Client
import crud.server.bank.db.table.ClientTable
import slick.ast.Ordering
import slick.jdbc.PostgresProfile.api._
import slick.lifted.ColumnOrdered

import java.time.LocalDate

case class GetManyClientsDbQuery(
  size: Int,
  page: Int,
  adult: Option[Boolean],
  sortBy: ClientSortField[_],
  sortType: SortType
) extends GetManyDbQuery[Client, ClientTable] {
  private def currentYear: Int = LocalDate.now.getYear

  override def sorted(q: Q): Q = {
    q.sortBy { c =>
      val r = sortBy.getRep(c)
      ColumnOrdered(r, Ordering()).sort(sortType)
    }
  }

  override def filtered(q: Q): Q =
    adult match {
      case Some(b) =>
        q.filter { client =>
          val age = client.birthYear * (-1) + currentYear
          if (b) age >= 18 else age < 18
        }
      case None => q
    }
}
