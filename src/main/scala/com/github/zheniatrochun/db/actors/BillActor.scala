package com.github.zheniatrochun.db.actors

import akka.actor.{Actor, ActorSystem}
import akka.util.Timeout
import akka.pattern.pipe
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
      pipe(db.run(billRepository.findOne(id))) to sender

    case FindAllBillsByUser(user) =>
      pipe(db.run(billRepository.findAllByUser(user))) to sender

    case FindAllBillsByUserPage(user, page) =>
      pipe(db.run(billRepository.findAllByUserAndPage(user, page))) to sender

    case CreateBill(bill) =>
      pipe(db.run(billRepository.save(bill))) to sender

    case DeleteBill(id) =>
      pipe(db.run(billRepository.deleteById(id))) to sender

    case UpdateBill(bill) =>
      pipe(db.run(billRepository.update(bill))) to sender

    case _ =>
      sender ! new UnsupportedOperationException
  }
}
