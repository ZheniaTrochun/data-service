package com.github.zheniatrochun

import akka.stream.ActorMaterializer
import akka.actor.{ActorSystem, Props}
import akka.event.{Logging, LoggingAdapter}
import akka.http.scaladsl.Http
import com.github.zheniatrochun.utils.AppConfig

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import akka.http.scaladsl.server.Directives._
import akka.util.Timeout
import com.github.zheniatrochun.api.{AdminRoutes, BillRoutes, UserRoutes}
import com.github.zheniatrochun.db.actors.{BillActor, DatabaseSupervisor, UserActor}
import com.github.zheniatrochun.db.repositories.UserRepository
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


  val dbSupervisor = system.actorOf(Props(new DatabaseSupervisor(dbConfig, dbConfig)))

  val userActor = system.actorOf(Props(new UserActor(dbConfig.db, new UserRepository(dbConfig.profile))))
//  val userService = new UserServiceImpl(userActor)
  val userService = new UserServiceImpl(dbSupervisor)
  val userRoutes = new UserRoutes(userService)

  val billActor = system.actorOf(Props(new BillActor(dbConfig)))
//  val billService = new BillServiceImpl(billActor)
  val billService = new BillServiceImpl(dbSupervisor)
  val billRoutes = new BillRoutes(billService)

  val adminService = new AdminServiceImpl(userActor, billActor)
  val adminRoutes = new AdminRoutes(adminService)

  Http().bindAndHandle(
    handler = logRequestResult("log")(userRoutes.routes ~ billRoutes.routes ~ adminRoutes.routes),
    interface = httpInterface,
    port = httpPort)
}