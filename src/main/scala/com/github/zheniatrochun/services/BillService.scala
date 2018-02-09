package com.github.zheniatrochun.services

import akka.actor.ActorRef
import com.github.zheniatrochun.models.requests._
import com.github.zheniatrochun.models.Bill
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

trait BillService {
  def create(bill: Bill): Future[Option[Int]]

  def update(bill: Bill): Future[Option[Bill]]

  def delete(id: Int): Future[Option[Int]]

  def getById(id: Int): Future[Option[Bill]]
}

class BillServiceImpl(val dbActor: ActorRef)
                     (implicit val timeout: Timeout)
  extends BillService {

  override def create(bill: Bill) = {
    dbActor ? CreateBill(bill) flatMap {
      case bill: Bill =>
        Future.successful(bill.id)

      case err: Exception =>
        Future.failed(err)

      case _ =>
        Future.failed(new InternalError())
    }
  }

  override def update(bill: Bill) = {
    dbActor ? UpdateBill(bill) flatMap {
      case bill: Bill =>
        Future.successful(Some(bill))

      case err: Exception =>
        Future.failed(err)

      case _ =>
        Future.failed(new InternalError())
    }
  }

  override def delete(id: Int) = {
    dbActor ? DeleteBill(id) flatMap {
      case id: Int =>
        Future.successful(Some(id))

      case err: Exception =>
        Future.failed(err)

      case _ =>
        Future.failed(new InternalError())
    }
  }

  override def getById(id: Int) = {
    dbActor ? FindBillById(id) flatMap {
      case Some(bill: Bill) =>
        Future.successful(Some(bill))

      case None =>
        Future.successful(None)

      case err: Exception =>
        Future.failed(err)

      case _ =>
        Future.failed(new InternalError())
    }
  }
}
