package crud.server.rest

import crud.server.api.db.PsqlDatabase

object TestDatabase extends PsqlDatabase("dbtest")
