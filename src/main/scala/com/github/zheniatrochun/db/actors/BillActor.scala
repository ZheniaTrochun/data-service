package com.github.zheniatrochun.db.actors

import akka.actor.{Actor, ActorSystem}
import akka.util.Timeout
import akka.pattern.pipe
import com.github.zheniatrochun.models.requests._
import com.github.zheniatrochun.db.repositories.BillRepository
import com.github.zheniatrochun.utils.ActorUtils
import org.slf4j.LoggerFactory
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

import scala.concurrent.ExecutionContext.Implicits.global


class BillActor(val dbConfig: DatabaseConfig[JdbcProfile])
               (implicit val system: ActorSystem, implicit val timeout: Timeout)
  extends Actor with ActorUtils {

  val logger = LoggerFactory.getLogger(this.getClass)

  val db = dbConfig.db
  val driver = dbConfig.profile

  val billRepository = new BillRepository(driver)

  override def receive = {
    case FindBillById(id) =>
      logger.debug(s"Received request: FindBillById($id)")
      pipe(db.run(billRepository.findOne(id))) to sender

    case FindAllBillsByUser(user) =>
      logger.debug(s"Received request: FindBillsByUser($user)")
      pipe(db.run(billRepository.findAllByUser(user))) to sender

    case FindAllBillsByUserPage(user, page) =>
      logger.debug(s"Received request: FindBillsByUser($user) page - $page")
      pipe(db.run(billRepository.findAllByUserAndPage(user, page))) to sender

    case CreateBill(bill) =>
      logger.debug(s"Received request: CreateBill($bill)")
      pipe(db.run(billRepository.save(bill))) to sender

    case DeleteBill(id) =>
      logger.debug(s"Received request: DeleteBill($id)")
      pipe(db.run(billRepository.deleteById(id))) to sender

    case UpdateBill(bill) =>
      logger.debug(s"Received request: UpdateBill($bill)")
      pipe(db.run(billRepository.update(bill))) to sender


    case CreateSchema =>
      logger.info(s"Received request: CreateSchema)")
      db.run { billRepository.setupSchema() }

    case DropSchema =>
      logger.info(s"Received request: DropSchema)")
      db.run { billRepository.dropSchema() }


    case _ =>
      logger.error(s"Received request of invalid type")
      sender ! new UnsupportedOperationException
  }
}
