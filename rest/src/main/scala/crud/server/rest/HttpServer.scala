package crud.server.rest

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import crud.server.api.db.query.{CreateDbQuery, GetManyDbQuery, UpdateDbQuery}
import crud.server.bank.db.entity.Client
import crud.server.bank.db.search.ClientSearchService
import crud.server.bank.db.table.ClientTable
import crud.server.rest.metrics.SystemMetrics.systemStartGauge

import scala.concurrent.{ExecutionContext, ExecutionContextExecutor}
import scala.io.StdIn

object HttpServer extends RoutesDef[GetManyDbQuery[Client, ClientTable], CreateDbQuery[Client, ClientTable], UpdateDbQuery[Client, ClientTable]] {
  implicit val system: ActorSystem                        = ActorSystem("bank-crud")
  implicit val executionContext: ExecutionContextExecutor = ExecutionContext.global
  override val clientSearchService = new ClientSearchService

  def run(): Unit = {
    val bindingFuture = Http().newServerAt("localhost", 8080).bind(routes)

    systemStartGauge.setToCurrentTime()

    println(s"Server is running. Press RETURN to stop...")
    StdIn.readLine()
    bindingFuture
      .flatMap(_.unbind())
      .onComplete(_ => system.terminate())
  }
}
