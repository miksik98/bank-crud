package crud.server.api.db.search

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import slick.ast.ScalaBaseType.intType
import slick.lifted.{ColumnOrdered, LiteralColumn, Rep}
import slick.ast.Ordering
import crud.server.api.db.DbImplicits.ColumnOrderedSortType
import crud.server.api.db.query.SortType

class DbImplicitsTest extends AnyFunSuite with Matchers {
  test("ColumnOrderedSortType#sort") {
    val co = ColumnOrdered(LiteralColumn(1), Ordering())
    co.sort(SortType.Asc).ord.direction shouldBe Ordering.Asc
    co.sort(SortType.Desc).ord.direction shouldBe Ordering.Desc
  }
}
