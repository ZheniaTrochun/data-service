package com.github.zheniatrochun.api

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpResponse, StatusCodes}
import akka.pattern.ask
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.github.zheniatrochun.db.models.User
import com.github.zheniatrochun.db.models.requests.CreateUser

import scala.concurrent.Future
import scala.util.Try

class UserRoutes(val dbActor: ActorRef)(implicit val system: ActorSystem) {

  val userRoutes = {
    path("users") {
      (path("create") & post) {
        entity(as[User]) { user =>
          completeWithFuture(createUser(user))
        }
      }
    }
  }

  def completeWithFuture(f: => Future[Option[User]]): Route = {
    onComplete(f) { res: Try[Option[User]] =>

      res.get match {
        case Some(user) =>
          val response = HttpResponse(StatusCodes.OK, entity = HttpEntity(ContentTypes.`application/json`, user.format))
          complete(user.toString)
      }
    }
  }

  def createUser(user: User): Future[Option[User]] = {
    dbActor ? CreateUser(user) flatMap {
      case res: Option[User] =>
        Future.successful(res)
    }
  }
}
