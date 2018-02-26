package com.github.zheniatrochun.services

import akka.actor.{ActorSystem, Props}
import akka.util.Timeout
import com.github.zheniatrochun.db.actors.{BillActor, UserActor}
import com.github.zheniatrochun.db.repositories.{BillRepository, UserRepository}
import com.github.zheniatrochun.models.requests.{CreateSchema, DropSchema}
import org.slf4j.LoggerFactory
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile


trait AdminService {
  def createTables(): Unit
  def dropTables(): Unit

  def dropUserTable(): Unit
  def dropBillTable(): Unit

  def createUserTable(): Unit
  def createBillTable(): Unit
}

class AdminServiceImpl(val dbConfig: DatabaseConfig[JdbcProfile])
                      (implicit val system: ActorSystem, implicit val timeout: Timeout)
  extends AdminService {

  val logger = LoggerFactory.getLogger(this.getClass)

  val db = dbConfig.db
  val userRepository = new UserRepository(dbConfig.profile)
  val billRepository = new BillRepository(dbConfig.profile)

  override def createTables(): Unit = {
    system.actorOf(Props(new UserActor(db, userRepository))) ! CreateSchema
    system.actorOf(Props(new BillActor(db, billRepository))) ! CreateSchema
  }

  override def dropTables(): Unit = {
    system.actorOf(Props(new UserActor(db, userRepository))) ! DropSchema
    system.actorOf(Props(new BillActor(db, billRepository))) ! DropSchema
  }


  override def dropUserTable(): Unit = {
    system.actorOf(Props(new UserActor(db, userRepository))) ! DropSchema
  }

  override def dropBillTable(): Unit = {
    system.actorOf(Props(new BillActor(db, billRepository))) ! DropSchema
  }


  override def createUserTable(): Unit = {
    system.actorOf(Props(new UserActor(db, userRepository))) ! CreateSchema
  }

  override def createBillTable(): Unit = {
    system.actorOf(Props(new BillActor(db, billRepository))) ! CreateSchema
  }
}
