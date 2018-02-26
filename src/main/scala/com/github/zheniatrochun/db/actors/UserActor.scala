package com.github.zheniatrochun.db.actors

import akka.actor.{Actor, ActorSystem}
import akka.pattern.pipe
import akka.util.Timeout
import com.github.zheniatrochun.db.repositories.UserRepository
import com.github.zheniatrochun.models.requests._
import com.github.zheniatrochun.exceptions.UserAlreadyExists
import org.slf4j.LoggerFactory
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

import scala.concurrent.Future

class UserActor(val dbConfig: DatabaseConfig[JdbcProfile])
               (implicit val system: ActorSystem, implicit val timeout: Timeout)
  extends Actor {

  import context.dispatcher

  val logger = LoggerFactory.getLogger(this.getClass)

  val db = dbConfig.db
  val driver = dbConfig.profile

  val userRepository = new UserRepository(driver)

  override def receive = {
    case FindUserById(id) =>
      logger.debug(s"Received request: FindUserById($id)")
      pipe(db.run(userRepository.findOne(id))) to sender

    case FindUserByName(name) =>
      logger.debug(s"Received request: FindUserByName($name)")
      pipe(db.run(userRepository.findOneByName(name))) to sender

    case FindUserByEmail(email) =>
      logger.debug(s"Received request: FindUserByEmail($email)")
      pipe(db.run(userRepository.findOneByEmail(email))) to sender

    case FindAllUsers =>
      logger.debug("Received request: FindAllUsers")
      pipe(db.run(userRepository.findAll())) to sender
      context.stop(self)

    case CreateUser(user) =>
      logger.debug(s"Received request: CreateUser($user)")
      pipe {
        db.run(userRepository.findOneByEmail(user.email)) flatMap  {
          case Some(_) =>
            logger.debug(s"User with mail = ${user.email} is already exists")
            Future.successful(UserAlreadyExists)

          case None =>
            db.run(userRepository.findOneByName(user.name)) flatMap {
              case Some(_) =>
                logger.debug(s"User with name = ${user.name} is already exists")
                Future.successful(UserAlreadyExists)

              case None =>
                logger.debug("Creating user ...")
                db.run(userRepository.save(user))
            }
        }
      } to sender
      context.stop(self)

    case DeleteUser(id) =>
      logger.debug(s"Received request: DeleteUser($id)")
      pipe(db.run(userRepository.deleteById(id))) to sender

    case UpdateUser(user) =>
      logger.debug(s"Received request: UpdateUser($user)")
      pipe(db.run(userRepository.update(user))) to sender


    case CreateSchema =>
      logger.info(s"Received request: CreateSchema)")
      db.run { userRepository.setupSchema() }

    case DropSchema =>
      logger.info(s"Received request: DropSchema)")
      db.run { userRepository.dropSchema() }


    case _ =>
      logger.error(s"Received request of invalid type")
      sender() ! new UnsupportedOperationException("Unsupported request!")
  }
}
