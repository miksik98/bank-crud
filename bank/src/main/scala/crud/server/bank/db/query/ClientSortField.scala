package crud.server.bank.db.query

import cats.data.{Validated, ValidatedNel}
import cats.implicits.catsSyntaxValidatedId
import crud.server.bank.db.table.ClientTable
import slick.lifted.Rep

sealed trait ClientSortField[T] {
  def getRep(table: ClientTable): Rep[T]
}

object ClientSortField {

  def fromString(s: String): Validated[String, ClientSortField[_]] = {
    s match {
      case "name"      => Name.valid
      case "surname"   => Surname.valid
      case "money"     => Money.valid
      case "birthYear" => BirthYear.valid
      case _           => s"Unknown sortBy type: $s".invalid
    }
  }

  case object Name extends ClientSortField[String] {
    override def getRep(table: ClientTable): Rep[String] = table.name
  }

  case object Surname extends ClientSortField[String] {
    override def getRep(table: ClientTable): Rep[String] = table.surname
  }

  case object Money extends ClientSortField[BigDecimal] {
    override def getRep(table: ClientTable): Rep[BigDecimal] = table.money
  }

  case object BirthYear extends ClientSortField[Int] {
    override def getRep(table: ClientTable): Rep[Int] = table.birthYear
  }

  case object Id extends ClientSortField[Int] {
    override def getRep(table: ClientTable): Rep[Int] = table.id
  }

}
