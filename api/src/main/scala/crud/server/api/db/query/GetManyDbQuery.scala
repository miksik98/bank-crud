package crud.server.api.db.query

import crud.server.api.db.entity.Entity
import crud.server.api.query.GetManyQuery
import slick.dbio.Effect
import slick.jdbc.PostgresProfile.api._
import slick.sql.FixedSqlStreamingAction

trait GetManyDbQuery[E <: Entity, T <: Table[E]] extends GetManyQuery {
  type Q = Query[T, E, Seq]
  protected def size: Int
  protected def page: Int
  protected def sorted(q: Q): Q   = q
  protected def filtered(q: Q): Q = q
  final def prepare(tableQuery: TableQuery[T]): FixedSqlStreamingAction[Seq[E], E, Effect.Read] = {
    val q         = sorted(filtered(tableQuery))
    val paginated = q.drop(page * size).take(size)
    paginated.result
  }
}
