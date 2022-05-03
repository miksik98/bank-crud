package crud.server.api.db.query

import crud.server.api.db.entity.Entity
import crud.server.api.query.CreateQuery
import slick.dbio.{DBIOAction, NoStream}
import slick.jdbc.PostgresProfile.api._
import slick.lifted.TableQuery

trait CreateDbQuery[E <: Entity, T <: Table[E]] extends CreateQuery {
  def prepare(tableQuery: TableQuery[T]): DBIOAction[Int, NoStream, Nothing]
}
