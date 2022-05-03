package crud.server.bank.db.query

import crud.server.api.db.query.UpdateDbQuery
import crud.server.bank.db.entity.Client
import crud.server.bank.db.table.ClientTable
import slick.lifted.TableQuery
import slick.jdbc.PostgresProfile.api._

case class UpdateClientDbQuery(id: Int, money: Double) extends UpdateDbQuery[Client, ClientTable] {
  override def prepare(tableQuery: TableQuery[ClientTable]) =
    sqlu"update client set money = money + $money where client_id = $id"
}
