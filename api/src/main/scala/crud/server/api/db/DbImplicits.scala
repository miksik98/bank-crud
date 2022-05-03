package crud.server.api.db

import crud.server.api.db.query.SortType
import slick.lifted.ColumnOrdered

object DbImplicits {
  implicit class ColumnOrderedSortType[T](val c: ColumnOrdered[T]) {
    def sort(sortType: SortType): ColumnOrdered[T] = {
      sortType match {
        case SortType.Asc  => c.asc
        case SortType.Desc => c.desc
      }
    }
  }
}
