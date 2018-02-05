package com.github.zheniatrochun.db.actors

import akka.actor.Actor
import com.github.zheniatrochun.db.repositories.UserRepository
import com.github.zheniatrochun.db.models.requests._
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

class UserActor(val dbConfig: DatabaseConfig[JdbcProfile]) extends Actor {

  val db = dbConfig.db
  val driver = dbConfig.profile

  val userRepository = new UserRepository(driver)

  override def receive = {
    case FindUserById(id) =>
      sender() ! db.run { userRepository.findOne(id) }

    case FindUserByName(name) =>
      sender() ! db.run { userRepository.findOneByName(name) }

    case FindUserByEmail(email) =>
      sender() ! db.run { userRepository.findOneByEmail(email) }

    case CreateUser(user) =>
      sender() ! db.run { userRepository.save(user) }

    case DeleteUser(id) =>
      sender() ! db.run { userRepository.deleteById(id) }

    case UpdateUser(user) =>
      sender() ! db.run { userRepository.update(user) }

    case _ =>
      sender() ! new UnsupportedOperationException("Unsupported request!")
  }
}
