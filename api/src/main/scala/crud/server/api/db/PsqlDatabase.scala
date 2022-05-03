package crud.server.api.db

import slick.dbio.{DBIOAction, NoStream}
import slick.jdbc.JdbcBackend.Database

import scala.concurrent.Future

abstract class PsqlDatabase(configPath: String) {
  private val db: Database = Database.forConfig(configPath)

  final def run[T](a: DBIOAction[T, NoStream, Nothing]): Future[T] = db.run(a)
}
