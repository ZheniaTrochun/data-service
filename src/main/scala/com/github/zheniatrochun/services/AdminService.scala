package com.github.zheniatrochun.services

import akka.actor.{ActorRef, ActorSystem}
import akka.util.Timeout
import com.github.zheniatrochun.models.requests.{CreateSchema, DropSchema}
import org.slf4j.LoggerFactory


trait AdminService {
  def createTables(): Unit
  def dropTables(): Unit
}

class AdminServiceImpl(val dbSupervisor: ActorRef)
                      (implicit val system: ActorSystem, implicit val timeout: Timeout)
  extends AdminService {

  val logger = LoggerFactory.getLogger(this.getClass)

  override def createTables(): Unit = {
    dbSupervisor ! CreateSchema
  }

  override def dropTables(): Unit = {
    dbSupervisor ! DropSchema
  }
}
