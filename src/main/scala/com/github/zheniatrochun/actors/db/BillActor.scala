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


class BillActor(val db: JdbcProfile#Backend#Database, val billRepository: BillRepository, val reqSender: ActorRef)
               (implicit val system: ActorSystem, implicit val timeout: Timeout)
  extends Actor {

  def this(db: JdbcProfile#Backend#Database, billRepository: BillRepository)
              (implicit system: ActorSystem, timeout: Timeout) = {
    this(db, billRepository, null)
  }

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

    case FindAllBillsByUserPage(user, page) =>
      logger.debug(s"Received request: FindBillsByUser($user) page - $page")
      pipe(db.run(billRepository.findAllByUserAndPage(user, page))) to sender
      context.stop(self)

    case CreateBill(bill, username) =>
      logger.debug(s"Received request: CreateBill($bill)")

      context.parent ? FindUserByName(username) foreach {
        case Some(user: User) =>
          pipe(db.run(billRepository.save(bill.copy(user = user.id.get)))) to reqSender
          context.stop(self)

        case _ =>
          None
          context.stop(self)
      }

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
