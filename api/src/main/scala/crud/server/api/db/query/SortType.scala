package crud.server.api.db.query

import cats.data.{Validated, ValidatedNel}
import cats.implicits.catsSyntaxValidatedId

sealed trait SortType

object SortType {
  def fromString(s: String): Validated[String, SortType] = {
    s match {
      case "asc"  => Asc.valid
      case "desc" => Desc.valid
      case _      => s"Unknown sort type: $s".invalid
    }
  }

  object Asc extends SortType

  object Desc extends SortType

}
