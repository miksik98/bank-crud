package crud.server.bank.db.table

import crud.server.bank.db.entity.Client
import slick.jdbc.PostgresProfile.api._

class ClientTable(tag: Tag) extends Table[Client](tag, "client") {
  def id        = column[Int]("client_id", O.PrimaryKey, O.AutoInc)
  def name      = column[String]("name")
  def surname   = column[String]("surname")
  def birthYear = column[Int]("birth_year")
  def money     = column[BigDecimal]("money", O.Default(0.0))

  override def * = (id, name, surname, birthYear, money) <> (Client.tupled, Client.unapply)
}

object ClientTable {
  val tableQuery = TableQuery[ClientTable]
}
