package com.github.zheniatrochun.services

import akka.actor.{ActorRef, ActorSystem}
import com.github.zheniatrochun.models.User
import akka.pattern.{AskableActorRef, ask}
import akka.util.Timeout
import com.github.zheniatrochun.exceptions.UserAlreadyExists
import com.github.zheniatrochun.models.requests._
import org.slf4j.LoggerFactory

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.ExecutionContext.Implicits.global

trait UserService {
  def create(user: User): Future[Option[Int]]

  def update(user: User): Future[Option[User]]

  def delete(id: Int): Future[Option[Int]]

  def getById(id: Int): Future[Option[User]]

  def getByName(name: String): Future[Option[User]]

  def getByEmail(email: String): Future[Option[User]]
}

class UserServiceImpl(val dbActor: AskableActorRef)
                     (implicit val timeout: Timeout)
  extends UserService {

  val logger = LoggerFactory.getLogger(this.getClass)

  override def create(user: User): Future[Option[Int]] = {
    dbActor ? CreateUser(user) flatMap {
      case id: Int =>
        logger.debug(s"User creation OK, id = $id")
        Future.successful(Some(id))

      case UserAlreadyExists =>
        logger.debug(s"User creation FAILED")
        Future.failed(new Exception("User is already exists!"))

      case _ =>
        logger.error(s"Error in actor model")
        Future.failed(new InternalError())
    }
  }

  override def update(user: User): Future[Option[User]] = {
    dbActor ? UpdateUser(user) flatMap {
      case user: User =>
        logger.debug(s"User updation OK, id = ${user.id}")
        Future.successful(Some(user))

      case _ =>
        logger.error(s"Error in actor model")
        Future.failed(new InternalError())
    }
  }

  override def delete(id: Int): Future[Option[Int]] = {
    dbActor ? DeleteUser(id) flatMap {
      case res: Int =>
        logger.debug(s"User deletion OK, id = $id")
        Future.successful(Some(res))

      case _ =>
        logger.error(s"Error in actor model")
        Future.failed(new InternalError())
    }
  }

  override def getById(id: Int): Future[Option[User]] = {
    dbActor ? FindUserById(id) flatMap {
      case Some(user: User) =>
        logger.debug(s"User getting by id OK, user = $user")
        Future.successful(Some(user))

      case None =>
        logger.debug(s"User getting by id FAILED")
        Future.successful(None)

      case _ =>
        logger.error(s"Error in actor model")
        Future.failed(new InternalError())
    }
  }

  override def getByName(name: String): Future[Option[User]] = {
    dbActor ? FindUserByName(name) flatMap {
      case Some(user: User) =>
        logger.debug(s"User getting by name OK, user = $user")
        Future.successful(Some(user))

      case None =>
        logger.debug(s"User getting by name FAILED")
        Future.successful(None)

      case _ =>
        logger.error(s"Error in actor model")
        Future.failed(new InternalError())
    }
  }

  override def getByEmail(email: String): Future[Option[User]] = {
    dbActor ? FindUserByEmail(email) flatMap {
      case Some(user: User) =>
        logger.debug(s"User getting by email OK, user = $user")
        Future.successful(Some(user))

      case None =>
        logger.debug(s"User getting by email FAILED")
        Future.successful(None)

      case _ =>
        logger.error(s"Error in actor model")
        Future.failed(new InternalError())
    }
  }
}
