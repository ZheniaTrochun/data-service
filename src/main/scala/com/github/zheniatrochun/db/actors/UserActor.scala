package com.github.zheniatrochun.db.actors

import akka.actor.{Actor, ActorSystem}
import akka.util.Timeout
import com.github.zheniatrochun.db.repositories.UserRepository
import com.github.zheniatrochun.models.requests._
import com.github.zheniatrochun.exceptions.UserAlreadyExistsException
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

import scala.concurrent.ExecutionContext.Implicits.global

class UserActor(val dbConfig: DatabaseConfig[JdbcProfile])
               (implicit val system: ActorSystem, implicit val timeout: Timeout)
  extends Actor {

  val db = dbConfig.db
  val driver = dbConfig.profile

  val userRepository = new UserRepository(driver)

  db.run { userRepository.setupSchema() }

  override def receive = {
    case FindUserById(id) =>
      sender() ! db.run { userRepository.findOne(id) }

    case FindUserByName(name) =>
      sender() ! db.run { userRepository.findOneByName(name) }

    case FindUserByEmail(email) =>
      sender() ! db.run { userRepository.findOneByEmail(email) }

    case CreateUser(user) =>
      db.run { userRepository.findOneByEmail(user.email) } foreach {
        case Some(_) =>
          sender() ! UserAlreadyExistsException

        case None =>
          sender() ! db.run { userRepository.save(user) }
      }

    case DeleteUser(id) =>
      sender() ! db.run { userRepository.deleteById(id) }

    case UpdateUser(user) =>
      sender() ! db.run { userRepository.update(user) }

    case _ =>
      sender() ! new UnsupportedOperationException("Unsupported request!")
  }
}
