package com.github.zheniatrochun.actors

import akka.actor.{Actor, ActorSystem, Props}
import akka.util.Timeout
import com.github.zheniatrochun.actors.db.{BillActor, UserActor, WalletActor}
import com.github.zheniatrochun.db.repositories.{BillRepository, UserRepository, WalletRepository}
import com.github.zheniatrochun.models.requests.{BillDatabaseRequest, GeneralDatabaseRequests, UserDatabaseRequest, WalletDatabaseRequest}
import com.github.zheniatrochun.utils.ActorMethodShortcuts
import org.slf4j.LoggerFactory
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.TimeoutException
import scala.language.postfixOps

class DatabaseSupervisor (val dbConfig: DatabaseConfig[JdbcProfile])
  (implicit val system: ActorSystem, implicit val timeout: Timeout)
    extends Actor with ActorMethodShortcuts {

  import akka.actor.OneForOneStrategy
  import akka.actor.SupervisorStrategy._

  import scala.concurrent.duration._

  val logger = LoggerFactory.getLogger(this.getClass)

  override val supervisorStrategy =
    OneForOneStrategy(maxNrOfRetries = 10, withinTimeRange = 1 minute) {
      case t: TimeoutException =>
        logger.warn("Timeout exception occurred!")
        Stop
      case _ =>
        Stop
    }

  val db = dbConfig.db
  val userRepository = new UserRepository(dbConfig.profile)
  val billRepository = new BillRepository(dbConfig.profile)
  val walletRepository = new WalletRepository(dbConfig.profile)

  override def receive = {
    case req: BillDatabaseRequest =>
      context.actorOf(Props(new BillActor(db, billRepository))) ~> req

    case req: UserDatabaseRequest =>
      context.actorOf(Props(new UserActor(db, userRepository))) ~> req

    case req: WalletDatabaseRequest =>
      context.actorOf(Props(new WalletActor(db, walletRepository))) ~> req

    case req: GeneralDatabaseRequests =>
      context.actorOf(Props(new UserActor(db, userRepository))) ! req
      context.actorOf(Props(new BillActor(db, billRepository))) ! req
      context.actorOf(Props(new WalletActor(db, walletRepository))) ! req

    case _ =>
      sender ! new InternalError("Invalid request to supervisor!")
  }
}
