package com.github.zheniatrochun.db.actors

import akka.actor.{Actor, ActorSystem}
import akka.util.Timeout
import com.github.zheniatrochun.models.requests._
import com.github.zheniatrochun.db.repositories.BillRepository
import com.github.zheniatrochun.utils.ActorUtils
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

import scala.concurrent.ExecutionContext.Implicits.global


class BillActor(val dbConfig: DatabaseConfig[JdbcProfile])
               (implicit val system: ActorSystem, implicit val timeout: Timeout)
  extends Actor with ActorUtils {

  val db = dbConfig.db
  val driver = dbConfig.profile

  val billRepository = new BillRepository(driver)

  db.run { billRepository.setupSchema() }

  override def receive = {
    case FindBillById(id) =>
      db.run { billRepository.findOne(id) } sendResponseTo sender

    case FindAllBillsByUser(user) =>
      db.run { billRepository.findAllByUser(user) } sendResponseTo sender

    case FindAllBillsByUserPage(user, page) =>
      db.run { billRepository.findAllByUserAndPage(user, page) } sendResponseTo sender

    case CreateBill(bill) =>
      db.run { billRepository.save(bill) } sendResponseTo sender

    case DeleteBill(id) =>
      db.run { billRepository.deleteById(id) } sendResponseTo sender

    case UpdateBill(bill) =>
      db.run { billRepository.update(bill) } sendResponseTo sender

    case _ =>
      sender ! new UnsupportedOperationException
  }
}
