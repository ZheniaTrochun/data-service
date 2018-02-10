package com.github.zheniatrochun

import akka.stream.ActorMaterializer
import akka.actor.{ActorSystem, Props}
import akka.event.{Logging, LoggingAdapter}
import akka.http.scaladsl.Http
import com.github.zheniatrochun.utils.Config

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import akka.http.scaladsl.server.Directives._
import akka.util.Timeout
import com.github.zheniatrochun.api.{BillRoutes, UserRoutes}
import com.github.zheniatrochun.db.actors.{BillActor, UserActor}
import com.github.zheniatrochun.models.requests.CreateSchema
import com.github.zheniatrochun.services.{BillServiceImpl, UserServiceImpl}
import slick.basic.DatabaseConfig

import scala.language.postfixOps

object Main extends App with Config with Routes {
  private implicit val system: ActorSystem = ActorSystem()
  protected implicit val executor: ExecutionContext = system.dispatcher
  protected val log: LoggingAdapter = Logging(system, getClass)
  protected implicit val materializer: ActorMaterializer = ActorMaterializer()
  private implicit val timeout: Timeout = 25 seconds

  val userActor = system.actorOf(Props(new UserActor(DatabaseConfig.forConfig("h2"))))
  val userService = new UserServiceImpl(userActor)
  val userRoutes = new UserRoutes(userService)
  userActor ! CreateSchema

  val billActor = system.actorOf(Props(new BillActor(DatabaseConfig.forConfig("h2"))))
  val billService = new BillServiceImpl(billActor)
  val billRoutes = new BillRoutes(billService)
  billActor ! CreateSchema

  Http().bindAndHandle(
    handler = logRequestResult("log")(routes ~ userRoutes.routes ~ billRoutes.routes),
    interface = httpInterface,
    port = httpPort)
}