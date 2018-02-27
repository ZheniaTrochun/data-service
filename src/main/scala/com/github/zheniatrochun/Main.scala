package com.github.zheniatrochun

import akka.stream.ActorMaterializer
import akka.actor.{ActorSystem, Props}
import akka.event.{Logging, LoggingAdapter}
import akka.http.scaladsl.Http

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import akka.http.scaladsl.server.Directives._
import akka.util.Timeout
import com.github.zheniatrochun.actors.DatabaseSupervisor
import com.github.zheniatrochun.api.{AdminRoutes, BillRoutes, UserRoutes}
import com.github.zheniatrochun.config.AppConfig
import com.github.zheniatrochun.services.{AdminServiceImpl, BillServiceImpl, UserServiceImpl}
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

import scala.language.postfixOps

object Main extends App with AppConfig {
  private implicit val system: ActorSystem = ActorSystem()
  protected implicit val executor: ExecutionContext = system.dispatcher
  protected val log: LoggingAdapter = Logging(system, getClass)
  protected implicit val materializer: ActorMaterializer = ActorMaterializer()
  private implicit val timeout: Timeout = 25 seconds

  private val dbConfig: DatabaseConfig[JdbcProfile] = DatabaseConfig.forConfig("postgres")

  val dbSupervisor = system.actorOf(Props(new DatabaseSupervisor(dbConfig)))

  val userService = new UserServiceImpl(dbSupervisor)
  val userRoutes = new UserRoutes(userService)

  val billService = new BillServiceImpl(dbSupervisor)
  val billRoutes = new BillRoutes(billService)

  val adminService = new AdminServiceImpl(dbSupervisor)
  val adminRoutes = new AdminRoutes(adminService)

  Http().bindAndHandle(
    handler = logRequestResult("log")(userRoutes.routes ~ billRoutes.routes ~ adminRoutes.routes),
    interface = httpInterface,
    port = httpPort)
}