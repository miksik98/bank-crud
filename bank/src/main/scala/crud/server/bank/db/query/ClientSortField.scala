package crud.server.bank.db.query

import cats.implicits.catsSyntaxOptionId
import crud.server.bank.db.table.ClientTable
import slick.lifted.Rep

sealed trait ClientSortField[T] {
  def getRep(table: ClientTable): Rep[T]
}

object ClientSortField {

  def fromString(s: String): Option[ClientSortField[_]] = {
    s match {
      case "name"      => Name.some
      case "surname"   => Surname.some
      case "money"     => Money.some
      case "birthYear" => BirthYear.some
      case _           => None
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
