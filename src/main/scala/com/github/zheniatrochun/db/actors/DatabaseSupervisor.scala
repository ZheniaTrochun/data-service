package com.github.zheniatrochun.db.actors

import akka.actor.{Actor, ActorSystem, Props}
import akka.pattern.{ask, pipe}
import akka.util.Timeout
import com.github.zheniatrochun.models.requests.{BillDatabaseRequest, UserDatabaseRequest}
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

import scala.concurrent.ExecutionContext.Implicits.global

import scala.language.postfixOps

class DatabaseSupervisor (val billDbConfig: DatabaseConfig[JdbcProfile], val userDbConfig: DatabaseConfig[JdbcProfile])
  (implicit val system: ActorSystem, implicit val timeout: Timeout)
    extends Actor {

  import akka.actor.OneForOneStrategy
  import akka.actor.SupervisorStrategy._
  import scala.concurrent.duration._

  override val supervisorStrategy =
    OneForOneStrategy(maxNrOfRetries = 10, withinTimeRange = 1 minute) {
      case t =>
        super.supervisorStrategy.decider.applyOrElse(t, (_: Any) => Restart)
    }

  override def receive = {
    case req: BillDatabaseRequest =>
      pipe(context.actorOf(Props(new BillActor(billDbConfig))) ? req) to sender

    case req: UserDatabaseRequest =>
      pipe(context.actorOf(Props(new BillActor(userDbConfig))) ? req) to sender

    case _ =>
      sender ! new InternalError("Invalid request to supervisor!")
  }
}
