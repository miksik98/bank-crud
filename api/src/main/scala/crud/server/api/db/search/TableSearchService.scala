package crud.server.api.db.search

import crud.server.api.db.PsqlDatabase
import crud.server.api.db.entity.Entity
import crud.server.api.db.query.{CreateDbQuery, GetManyDbQuery, UpdateDbQuery}
import crud.server.api.search.{AbstractCreateQueryForm, AbstractManyQueryForm, AbstractUpdateQueryForm, SearchService}
import slick.dbio.{DBIOAction, NoStream}
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.Future

abstract class TableSearchService[
  E <: Entity,
  T <: Table[E],
  MQF <: AbstractManyQueryForm,
  CQF <: AbstractCreateQueryForm,
  UQF <: AbstractUpdateQueryForm
](db: PsqlDatabase, tableQuery: TableQuery[T])
  extends SearchService[E, GetManyDbQuery[E, T], CreateDbQuery[E, T], UpdateDbQuery[E, T], MQF, CQF, UQF] {
  protected def getAction(id: Id): DBIOAction[Option[E], NoStream, Nothing]
  protected def deleteAction(id: Id): DBIOAction[Int, NoStream, Nothing]

  final def getMany(query: GetManyDbQuery[E, T]): Future[Seq[E]] = db.run(query.prepare(tableQuery))
  final def get(id: Id): Future[Option[E]]                       = db.run(getAction(id))
  final def create(query: CreateDbQuery[E, T]): Future[Int]      = db.run(query.prepare(tableQuery))
  final def update(query: UpdateDbQuery[E, T]): Future[Int]      = db.run(query.prepare(tableQuery))
  final def delete(id: Id): Future[Int]                          = db.run(deleteAction(id))
}
