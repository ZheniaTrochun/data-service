package com.github.zheniatrochun.services

import akka.actor.{ActorRef, ActorSystem}
import com.github.zheniatrochun.models.User
import akka.pattern.ask
import akka.util.Timeout
import com.github.zheniatrochun.models.requests._

import scala.concurrent.Future

import scala.concurrent.ExecutionContext.Implicits.global


trait UserService {
  def create(user: User): Future[Option[Int]]

  def update(user: User): Future[Option[User]]

  def delete(id: Int): Future[Option[Int]]

  def getById(id: Int): Future[Option[User]]

  def getByName(name: String): Future[Option[User]]

  def getByEmail(email: String): Future[Option[User]]
}

class UserServiceImpl(val dbActor: ActorRef)(implicit val timeout: Timeout)
  extends UserService {

  override def create(user: User): Future[Option[Int]] = {
    dbActor ? CreateUser(user) flatMap {
      case user: User =>
        Future.successful(user.id)

      case err: Exception =>
        Future.failed(err)

      case _ =>
        Future.failed(new InternalError())
    }
  }

  override def update(user: User): Future[Option[User]] = {
    dbActor ? UpdateUser(user) flatMap {
      case user: User =>
        Future.successful(Some(user))

      case err: Exception =>
        Future.failed(err)

      case _ =>
        Future.failed(new InternalError())
    }
  }

  override def delete(id: Int): Future[Option[Int]] = {
    dbActor ? DeleteUser(id) flatMap {
      case res: Int =>
        Future.successful(Some(res))

      case _ =>
        Future.failed(new InternalError())
    }
  }

  override def getById(id: Int): Future[Option[User]] = {
    dbActor ? FindUserById(id) flatMap {
      case Some(user: User) =>
        Future.successful(Some(user))

      case None =>
        Future.successful(None)

      case _ =>
        Future.failed(new InternalError())
    }
  }

  override def getByName(name: String): Future[Option[User]] = {
    dbActor ? FindUserByName(name) flatMap {
      case Some(user: User) =>
        Future.successful(Some(user))

      case None =>
        Future.successful(None)

      case _ =>
        Future.failed(new InternalError())
    }
  }

  override def getByEmail(email: String): Future[Option[User]] = {
    dbActor ? FindUserByEmail(email) flatMap {
      case Some(user: User) =>
        Future.successful(Some(user))

      case None =>
        Future.successful(None)

      case _ =>
        Future.failed(new InternalError())
    }
  }
}
