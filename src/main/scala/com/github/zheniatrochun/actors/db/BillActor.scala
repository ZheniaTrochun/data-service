package com.github.zheniatrochun.actors.db

import akka.actor.{Actor, ActorRef, ActorSystem}
import akka.pattern.{ask, pipe}
import akka.util.Timeout
import com.github.zheniatrochun.db.repositories.BillRepository
import com.github.zheniatrochun.models.User
import com.github.zheniatrochun.models.requests._
import org.slf4j.LoggerFactory
import slick.jdbc.JdbcProfile

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


class BillActor(val db: JdbcProfile#Backend#Database, val billRepository: BillRepository)
               (implicit val system: ActorSystem, implicit val timeout: Timeout)
  extends Actor {

  val logger = LoggerFactory.getLogger(this.getClass)

  override def receive = {
    case FindBillById(id) =>
      logger.debug(s"Received request: FindBillById($id)")
      pipe(db.run(billRepository.findOne(id))) to sender
      context.stop(self)

    case FindAllBillsByUser(user) =>
      logger.debug(s"Received request: FindBillsByUser($user)")
      pipe(db.run(billRepository.findAllByUser(user))) to sender
      context.stop(self)

    case FindAllBillsByUsername(username) =>
      logger.debug(s"Received request: FindBillsByUsername($username)")
      context.parent ? FindUserByName(username) flatMap {
        case Some(user: User) =>
          db.run(billRepository.findAllByUser(user.id.get))

        case None =>
          Future.successful(None)
      } pipeTo sender
      context.stop(self)

    case FindAllBillsByUserPage(user, page) =>
      logger.debug(s"Received request: FindBillsByUser($user) page - $page")
      pipe(db.run(billRepository.findAllByUserAndPage(user, page))) to sender
      context.stop(self)

    case CreateBill(bill, username) =>
      logger.debug(s"Received request: CreateBill($bill)")

      val parent = context.parent
      pipe {
        parent ? FindUserByName(username) flatMap {
          case Some(user: User) =>
            logger.debug(s"User found, saving bill")
            bill.wallet foreach { wallet: Int =>
              parent ! UpdateWalletBalance(wallet, bill.amount)
            }

            db.run(billRepository.save(bill.copy(user = user.id.get)))

          case _ =>
            logger.warn(s"User not found! :(")
            Future.successful(None)
        }
      } to sender

      context.stop(self)

    case DeleteBill(id) =>
      logger.debug(s"Received request: DeleteBill($id)")
      pipe(db.run(billRepository.deleteById(id))) to sender
      context.stop(self)

    case UpdateBill(bill) =>
      logger.debug(s"Received request: UpdateBill($bill)")
      pipe(db.run(billRepository.update(bill))) to sender
      context.stop(self)

    case FindAllBills =>
      logger.debug(s"Received request: FindAllBills")
      pipe(db.run(billRepository.findAll())) to sender
      context.stop(self)


    case CreateSchema =>
      logger.info(s"Received request: CreateSchema")
      db.run { billRepository.setupSchema() }
      context.stop(self)

    case DropSchema =>
      logger.info(s"Received request: DropSchema")
      db.run { billRepository.dropSchema() }
      context.stop(self)


    case _ =>
      logger.error(s"Received request of invalid type")
      sender ! new UnsupportedOperationException
      context.stop(self)
  }
}
