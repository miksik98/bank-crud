package crud.server.bank.db.search

import crud.server.api.search.AbstractCreateQueryForm

case class CreateQueryForm(name: String, surname: String, birthYear: Int) extends AbstractCreateQueryForm
