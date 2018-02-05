package com.github.zheniatrochun.db.actors

import akka.actor.Actor
import com.github.zheniatrochun.db.repositories.UserRepository
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

class UserActor(val dbConfig: DatabaseConfig[JdbcProfile]) extends Actor {

  val db = dbConfig.db
  val driver = dbConfig.profile

  val userRepository = new UserRepository(driver)

  override def receive = {
    case _ =>
      sender() ! "pong"
  }
}
