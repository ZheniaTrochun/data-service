package com.github.zheniatrochun.db.actors

import akka.actor.Actor
import com.github.zheniatrochun.models.Bill
import com.github.zheniatrochun.models.requests._
import com.github.zheniatrochun.db.repositories.BillRepository
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile


class BillActor(val dbConfig: DatabaseConfig[JdbcProfile]) extends Actor {

  val db = dbConfig.db
  val driver = dbConfig.profile

  val billRepository = new BillRepository(driver)

  override def receive = {
    case FindBillById(id) =>
      sender() ! db.run { billRepository.findOne(id) }

    case FindAllBillsByUser(user) =>
      sender() ! db.run { billRepository.findAllByUser(user) }

    case FindAllBillsByUserPage(user, page) =>
      sender() ! db.run { billRepository.findAllByUserAndPage(user, page) }

    case CreateBill(bill) =>
      sender() ! db.run { billRepository.save(bill) }

    case DeleteBill(id) =>
      sender() ! db.run { billRepository.deleteById(id) }

    case UpdateBill(bill) =>
      sender() ! db.run { billRepository.update(bill) }

    case _ =>
      sender() ! new UnsupportedOperationException
  }
}
