package crud.server.api.db.query

import cats.implicits.catsSyntaxOptionId

sealed trait SortType

object SortType {
  def fromString(s: String): Option[SortType] = {
    s match {
      case "asc"  => Asc.some
      case "desc" => Desc.some
      case _      => None
    }
  }

  object Asc extends SortType

  object Desc extends SortType

}
