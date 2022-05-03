package crud.server.api.query

import crud.server.api.db.entity.Entity

/**
 * Stands for queries to perform on dataset
 */
sealed trait AbstractQuery

trait CreateQuery extends AbstractQuery

trait GetManyQuery extends AbstractQuery

trait UpdateQuery[E <: Entity] extends AbstractQuery {
  def id: E#Id
}
