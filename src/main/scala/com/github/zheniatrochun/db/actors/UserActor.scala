package com.github.zheniatrochun.db.actors

import akka.actor.{Actor, ActorSystem}
import akka.pattern.pipe
import akka.util.Timeout
import com.github.zheniatrochun.db.repositories.UserRepository
import com.github.zheniatrochun.models.requests._
import com.github.zheniatrochun.exceptions.UserAlreadyExists
import com.github.zheniatrochun.utils.ActorUtils
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

import scala.concurrent.Future

class UserActor(val dbConfig: DatabaseConfig[JdbcProfile])
               (implicit val system: ActorSystem, implicit val timeout: Timeout)
  extends Actor with ActorUtils {

  import context.dispatcher

  val db = dbConfig.db
  val driver = dbConfig.profile

  val userRepository = new UserRepository(driver)

  db.run { userRepository.setupSchema() }

  override def receive = {
    case FindUserById(id) =>
      pipe(db.run(userRepository.findOne(id))) to sender

    case FindUserByName(name) =>
      pipe(db.run(userRepository.findOneByName(name))) to sender

    case FindUserByEmail(email) =>
      pipe(db.run(userRepository.findOneByEmail(email))) to sender

    case CreateUser(user) =>
      pipe {
        db.run {
          userRepository.findOneByEmail(user.email)
        } flatMap  {
          case Some(_) =>
            Future.successful(UserAlreadyExists)

          case None =>
            db.run {
              userRepository.save(user)
            }
        }
      } to sender

    case DeleteUser(id) =>
      pipe(db.run(userRepository.deleteById(id))) to sender

    case UpdateUser(user) =>
      pipe(db.run(userRepository.update(user))) to sender

    case _ =>
      sender() ! new UnsupportedOperationException("Unsupported request!")
  }
}
