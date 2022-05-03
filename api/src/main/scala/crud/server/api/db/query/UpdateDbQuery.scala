package crud.server.api.db.query

import crud.server.api.db.entity.Entity
import crud.server.api.query.UpdateQuery
import slick.jdbc.PostgresProfile.api._
import slick.lifted.TableQuery

trait UpdateDbQuery[E <: Entity, T <: Table[E]] extends UpdateQuery[E] {
  def prepare(tableQuery: TableQuery[T]): DBIOAction[Int, NoStream, Nothing]
}
