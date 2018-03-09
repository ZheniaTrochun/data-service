package com.github.zheniatrochun.services

import akka.actor.ActorRef
import akka.http.scaladsl.client.RequestBuilding
import akka.http.scaladsl.model.HttpResponse
import com.github.zheniatrochun.models.User
import com.github.zheniatrochun.validators.UserValidator._
import akka.pattern.ask
import akka.util.Timeout
import com.github.zheniatrochun.exceptions.UserAlreadyExists
import com.github.zheniatrochun.models.requests._
import org.slf4j.LoggerFactory

import scala.concurrent.{Future, Promise}
import scala.concurrent.ExecutionContext.Implicits.global

trait UserService {
  def createDB(): Unit

  def create(user: User): Future[Option[Int]]

  def update(user: User): Future[Option[User]]

  def delete(id: Int): Future[Boolean]

  def deleteByName(name: String): Future[Boolean]

  def getById(id: Int): Future[Option[User]]

  def getByName(name: String): Future[Option[User]]

  def getByEmail(email: String): Future[Option[User]]

  def getAll(): Future[List[User]]
}

class UserServiceImpl(val dbActor: ActorRef, val httpActor: ActorRef)
                     (implicit val timeout: Timeout)
  extends UserService {

  val logger = LoggerFactory.getLogger(this.getClass)

  override def create(user: User): Future[Option[Int]] = {

    // 1 - check fields validation
    val valid = user.isValid
    if (!valid) {
    // None -> if something not valid
      Future.successful(None)
    } else {

      dbActor ? CreateUser(user) flatMap {
        case user: User =>
          logger.debug(s"User creation OK, id = ${user.id}")
          Future.successful(user.id)

        case UserAlreadyExists =>
          logger.debug(s"User creation FAILED")
          Future.failed(new Exception("User is already exists!"))

        case _ =>
          logger.error(s"Error in actor model")
          Future.failed(new InternalError())
      }
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

  override def delete(id: Int): Future[Boolean] = {
    dbActor ? DeleteUser(id) flatMap {
      case res: Int =>
        logger.debug(s"User deletion OK, number = $res")
        Future.successful(res != 0)

      case _ =>
        logger.error(s"Error in actor model")
        Future.failed(new InternalError())
    }
  }

  override def deleteByName(name: String): Future[Boolean] = {
    dbActor ? DeleteUserByName(name) flatMap {
      case res: Int =>
        logger.debug(s"User deletion OK, number = $res")

//        if deleted something we need to delete creds too
        if (res != 0) {
          val promise: Promise[HttpResponse] = Promise[HttpResponse]()
          httpActor ! SendRequestToAuth(promise, RequestBuilding.Delete(s"/auth/user?name=$name"))

          Future.successful(true)
        } else {
          Future.successful(false)
        }

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

  override def getAll(): Future[List[User]] = {
    dbActor ? FindAllUsers flatMap {
      case Seq() =>
        logger.debug("No users already exists")
        Future.successful(Nil)

      case res: Seq[User] =>
        logger.debug(s"User get all - OK, length = ${res.length}")
        Future.successful(res.toList)

      case _ =>
        logger.error(s"Error in actor model")
        Future.failed(new InternalError())
    }
  }

  override def createDB(): Unit = {
    dbActor ! CreateSchema
  }
}
