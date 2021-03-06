package com.github.zheniatrochun

import akka.stream.ActorMaterializer
import akka.actor.{ActorSystem, Props}
import akka.event.{Logging, LoggingAdapter}
import akka.http.scaladsl.Http

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import akka.http.scaladsl.server.Directives._
import akka.util.Timeout
import com.github.zheniatrochun.actors.{DatabaseSupervisor, HttpActor, MessageQueueActor}
import com.github.zheniatrochun.api._
import com.github.zheniatrochun.config.AppConfig
import com.github.zheniatrochun.services.{AdminServiceImpl, BillServiceImpl, UserServiceImpl, WalletServiceImpl}
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

import scala.language.postfixOps

object Main extends App with AppConfig with HealthRoutes {
  private implicit val system: ActorSystem = ActorSystem()
  protected implicit val executor: ExecutionContext = system.dispatcher
  protected val log: LoggingAdapter = Logging(system, getClass)
  protected implicit val materializer: ActorMaterializer = ActorMaterializer()
  private implicit val timeout: Timeout = 25 seconds

  private val dbConfig: DatabaseConfig[JdbcProfile] = DatabaseConfig.forConfig("postgres")

  val dbSupervisor = system.actorOf(Props(new DatabaseSupervisor(dbConfig)))
  val httpActor = system.actorOf(Props(new HttpActor()))
  val mqActor = system.actorOf(Props(new MessageQueueActor()))

  val userService = new UserServiceImpl(dbSupervisor, httpActor)
  val userRoutes = new UserRoutes(userService)

  val billService = new BillServiceImpl(dbSupervisor, mqActor, httpActor)
  val billRoutes = new BillRoutes(billService)

  val adminService = new AdminServiceImpl(dbSupervisor)
  val adminRoutes = new AdminRoutes(adminService)

  val walletService = new WalletServiceImpl(dbSupervisor)
  val walletRoutes = new WalletRoutes(walletService)

  Http().bindAndHandle(
    handler = logRequestResult("log")(
        userRoutes.routes ~
        billRoutes.routes ~
        walletRoutes.routes ~
        adminRoutes.routes ~
        healthRoutes),
    interface = httpInterface,
    port = httpPort)
}