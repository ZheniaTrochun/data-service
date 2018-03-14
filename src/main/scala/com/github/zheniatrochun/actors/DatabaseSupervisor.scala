package com.github.zheniatrochun.actors

import akka.actor.{Actor, ActorSystem, Props}
import akka.util.Timeout
import com.github.zheniatrochun.actors.db.{BillActor, UserActor}
import com.github.zheniatrochun.db.repositories.{BillRepository, UserRepository}
import com.github.zheniatrochun.models.requests.{BillDatabaseRequest, GeneralDatabaseRequests, UserDatabaseRequest}
import com.github.zheniatrochun.utils.ActorMethodShortcuts
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

import scala.concurrent.ExecutionContext.Implicits.global
import scala.language.postfixOps

class DatabaseSupervisor (val dbConfig: DatabaseConfig[JdbcProfile])
  (implicit val system: ActorSystem, implicit val timeout: Timeout)
    extends Actor with ActorMethodShortcuts {

  import akka.actor.OneForOneStrategy
  import akka.actor.SupervisorStrategy._

  import scala.concurrent.duration._

  override val supervisorStrategy =
    OneForOneStrategy(maxNrOfRetries = 10, withinTimeRange = 1 minute) {
      case t =>
        super.supervisorStrategy.decider.applyOrElse(t, (_: Any) => Restart)
    }

  val db = dbConfig.db
  val userRepository = new UserRepository(dbConfig.profile)
  val billRepository = new BillRepository(dbConfig.profile)

  override def receive = {
    case req: BillDatabaseRequest =>
      context.actorOf(Props(new BillActor(db, billRepository))) ~> req

    case req: UserDatabaseRequest =>
      context.actorOf(Props(new UserActor(db, userRepository))) ~> req

    case req: GeneralDatabaseRequests =>
      context.actorOf(Props(new UserActor(db, userRepository))) ! req
      context.actorOf(Props(new BillActor(db, billRepository))) ! req

    case _ =>
      sender ! new InternalError("Invalid request to supervisor!")
  }
}
