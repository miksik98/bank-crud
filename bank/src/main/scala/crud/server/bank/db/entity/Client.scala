package crud.server.bank.db.entity

import crud.server.api.db.entity.Entity

case class Client(
  id: Int,
  name: String,
  surname: String,
  birthYear: Int,
  money: BigDecimal
) extends Entity {
  override type Id = Int
}
