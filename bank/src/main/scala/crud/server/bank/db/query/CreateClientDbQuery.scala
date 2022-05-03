package crud.server.bank.db.query

import crud.server.api.db.query.CreateDbQuery
import crud.server.bank.db.entity.Client
import crud.server.bank.db.table.ClientTable
import slick.jdbc.PostgresProfile
import slick.jdbc.PostgresProfile.api._

case class CreateClientDbQuery(name: String, surname: String, birthYear: Int) extends CreateDbQuery[Client, ClientTable] {
  override def prepare(tableQuery: PostgresProfile.api.TableQuery[ClientTable]) =
    sqlu"insert into client (name, surname, birth_year) values ($name, $surname, $birthYear)"
}
