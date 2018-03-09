package com.github.zheniatrochun.services

import akka.actor.ActorRef
import com.github.zheniatrochun.models.requests._
import com.github.zheniatrochun.models.{Bill, BillBuilder, User}
import akka.pattern.ask
import akka.util.Timeout
import com.github.zheniatrochun.models.dto.BillDto
import org.slf4j.LoggerFactory

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.language.postfixOps

trait BillService {
  def create(dto: BillDto, user: String): Future[Option[Int]]

  def update(bill: Bill): Future[Option[Bill]]

  def delete(id: Int): Future[Option[Int]]

  def getById(id: Int): Future[Option[Bill]]

  def getAllByUser(User: Int): Future[List[Bill]]

  def getAll(): Future[List[Bill]]
}

class BillServiceImpl(val dbActor: ActorRef)
                     (implicit val timeout: Timeout)
  extends BillService {

  val logger = LoggerFactory.getLogger(this.getClass)

  override def create(dto: BillDto, username: String): Future[Option[Int]] = {
    dbActor ? FindUserByName(username) flatMap {
      case user: User =>
        dbActor ? CreateBill(BillBuilder(dto).withUser(user.id.get).build()) flatMap {
          case bill: Bill =>
            logger.debug(s"Bill creation by id OK, bill = $bill")
            Future.successful(Some(bill.id.get))

          case _ =>
            logger.error(s"Error in actor model")
            Future.failed(new InternalError())
        }

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

  override def getAllByUser(user: Int) = {
    dbActor ? FindAllBillsByUser(user) flatMap {
      case res: Seq[Bill] =>
        logger.debug(s"Bill getting all by user($user) OK, length = ${res.length}")
        Future.successful(res toList)

      case _ =>
        logger.error(s"Error in actor model")
        Future.failed(new InternalError())
    }
  }

  override def getAll() = {
    dbActor ? FindAllBills flatMap {
      case res: Seq[Bill] =>
        logger.debug(s"Bill getting all OK, length = ${res.length}")
        Future.successful(res toList)

      case _ =>
        logger.error(s"Error in actor model")
        Future.failed(new InternalError())
    }
  }
}
