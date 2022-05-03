package crud.server.bank.db.search

import crud.server.api.search.AbstractUpdateQueryForm

case class UpdateQueryForm(id: Int, money: Double) extends AbstractUpdateQueryForm
