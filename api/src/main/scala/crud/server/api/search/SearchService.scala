package crud.server.api.search

import cats.data.{NonEmptyList, Validated, ValidatedNel}
import crud.server.api.db.entity.Entity
import crud.server.api.query.{CreateQuery, GetManyQuery, UpdateQuery}

import scala.concurrent.Future

trait SearchService[
  E <: Entity,
  M <: GetManyQuery,
  C <: CreateQuery,
  U <: UpdateQuery[E],
  MQF <: AbstractManyQueryForm,
  CQF <: AbstractCreateQueryForm,
  UQF <: AbstractUpdateQueryForm
] {
  final type Id = E#Id
  final type AllErrorsOr[A] = ValidatedNel[String, A]

  def validateGetManyQuery(form: MQF): AllErrorsOr[M]
  def validateCreateQuery(form: CQF): AllErrorsOr[C]
  def validateUpdateQuery(form: UQF): AllErrorsOr[U]

  def getMany(query: M): Future[Seq[E]]
  def get(id: Id): Future[Option[E]]

  /**
   * @return the number of affected records
   */
  def create(query: C): Future[Int]

  /**
   * @return the number of affected records
   */
  def update(query: U): Future[Int]

  /**
   * @return the number of affected records
   */
  def delete(id: Id): Future[Int]
}
