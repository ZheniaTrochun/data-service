package com.github.zheniatrochun.db.actors

import akka.actor.{Actor, ActorSystem}
import akka.util.Timeout
import com.github.zheniatrochun.db.repositories.UserRepository
import com.github.zheniatrochun.models.requests._
import com.github.zheniatrochun.exceptions.UserAlreadyExistsException
import com.github.zheniatrochun.utils.ActorUtils
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

import scala.concurrent.ExecutionContext.Implicits.global

class UserActor(val dbConfig: DatabaseConfig[JdbcProfile])
               (implicit val system: ActorSystem, implicit val timeout: Timeout)
  extends Actor with ActorUtils {

  val db = dbConfig.db
  val driver = dbConfig.profile

  val userRepository = new UserRepository(driver)

  db.run { userRepository.setupSchema() }

  override def receive = {
    case FindUserById(id) =>
      db.run { userRepository.findOne(id) } sendResponseTo sender

    case FindUserByName(name) =>
      db.run { userRepository.findOneByName(name) } sendResponseTo sender

    case FindUserByEmail(email) =>
      db.run { userRepository.findOneByEmail(email) } sendResponseTo sender

    case CreateUser(user) =>
      db.run { userRepository.findOneByEmail(user.email) } foreach {
        case Some(_) =>
          sender ! UserAlreadyExistsException

        case None =>
          db.run { userRepository.save(user) } sendResponseTo sender
      }

    case DeleteUser(id) =>
      db.run { userRepository.deleteById(id) } sendResponseTo sender

    case UpdateUser(user) =>
      db.run { userRepository.update(user) } sendResponseTo sender

    case _ =>
      sender() ! new UnsupportedOperationException("Unsupported request!")
  }
}
