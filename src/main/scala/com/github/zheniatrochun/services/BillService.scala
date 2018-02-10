package com.github.zheniatrochun.services

import akka.actor.ActorRef
import com.github.zheniatrochun.models.requests._
import com.github.zheniatrochun.models.Bill
import akka.pattern.{AskableActorRef, ask}
import akka.util.Timeout
import org.slf4j.LoggerFactory

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

trait BillService {
  def create(bill: Bill): Future[Option[Int]]

  def update(bill: Bill): Future[Option[Bill]]

  def delete(id: Int): Future[Option[Int]]

  def getById(id: Int): Future[Option[Bill]]
}

class BillServiceImpl(val dbActor: AskableActorRef)
                     (implicit val timeout: Timeout)
  extends BillService {

  val logger = LoggerFactory.getLogger(this.getClass)

  override def create(bill: Bill) = {
    dbActor ? CreateBill(bill) flatMap {
      case id: Int =>
        logger.debug(s"Bill getting by id OK, id = $id")
        Future.successful(Some(id))

      case _ =>
        logger.error(s"Error in actor model")
        Future.failed(new InternalError())
    }
  }

  override def update(bill: Bill) = {
    dbActor ? UpdateBill(bill) flatMap {
      case bill: Bill =>
        logger.debug(s"Bill updating OK, id = ${bill.id}")
        Future.successful(Some(bill))

      case _ =>
        logger.error(s"Error in actor model")
        Future.failed(new InternalError())
    }
  }

  override def delete(id: Int) = {
    dbActor ? DeleteBill(id) flatMap {
      case id: Int =>
        logger.debug(s"Bill deletion OK, id = $id")
        Future.successful(Some(id))

      case _ =>
        logger.error(s"Error in actor model")
        Future.failed(new InternalError())
    }
  }

  override def getById(id: Int) = {
    dbActor ? FindBillById(id) flatMap {
      case Some(bill: Bill) =>
        logger.debug(s"Bill getting by id OK, bill = $bill")
        Future.successful(Some(bill))

      case None =>
        logger.debug(s"Bill getting by id FAILED")
        Future.successful(None)

      case _ =>
        logger.error(s"Error in actor model")
        Future.failed(new InternalError())
    }
  }
}
