package com.github.zheniatrochun.services

import akka.actor.ActorRef
import akka.util.Timeout
import com.github.zheniatrochun.models.requests.{CreateSchema, DropSchema}
import org.slf4j.LoggerFactory


trait AdminService {
  def createTables(): Unit
  def dropTables(): Unit

  def dropUserTable(): Unit
  def dropBillTable(): Unit

  def createUserTable(): Unit
  def createBillTable(): Unit
}

class AdminServiceImpl(val dbActorUser: ActorRef,
                       val dbActorBill: ActorRef)
                      (implicit val timeout: Timeout)
  extends AdminService {

  val logger = LoggerFactory.getLogger(this.getClass)

  override def createTables(): Unit = {
    dbActorUser ! CreateSchema
    dbActorBill ! CreateSchema
  }

  override def dropTables(): Unit = {
    dbActorUser ! DropSchema
    dbActorBill ! DropSchema
  }


  override def dropUserTable(): Unit = {
    dbActorUser ! DropSchema
  }

  override def dropBillTable(): Unit = {
    dbActorBill ! DropSchema
  }


  override def createUserTable(): Unit = {
    dbActorUser ! CreateSchema
  }

  override def createBillTable(): Unit = {
    dbActorBill ! CreateSchema
  }
}
