package crud.server.bank.db.search

import crud.server.api.search.AbstractManyQueryForm

case class ManyQueryForm(size: Int, page: Int, adult: Option[Boolean], sortBy: Option[String], sortType: String)
  extends AbstractManyQueryForm
