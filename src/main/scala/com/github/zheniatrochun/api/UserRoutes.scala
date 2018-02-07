package com.github.zheniatrochun.api

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.model._
import akka.pattern.ask
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.github.zheniatrochun.models.User
import com.github.zheniatrochun.models.requests.CreateUser
import spray.json._
import com.github.zheniatrochun.models.json.JsonProtocol

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


  def completeWithFuture(f: => Future[Option[Any]]): Route = {
    onComplete(f) {
      case res: Try[Option[User]] =>
        res.get match {
          case Some(user) =>
            val response = HttpResponse(StatusCodes.OK, entity = buildEntity(user))
            complete(response)

          case None =>
            val response = HttpResponse(StatusCodes.BadRequest)
            complete(response)
        }


      case res: Try[Option[Int]] =>
        res.get match {
          case Some(id) =>
            val response = HttpResponse(StatusCodes.OK, entity = buildEntity(id))
            complete(response)

          case None =>
            val response = HttpResponse(StatusCodes.BadRequest)
            complete(response)
        }

      case _ =>
        val response = HttpResponse(StatusCodes.InternalServerError)
        complete(response)
    }
  }

  def buildEntity(user: User): ResponseEntity = {
    HttpEntity(user.toJson.toString).withContentType(ContentType(MediaTypes.`application/json`))
  }

  def buildEntity(id: Int): ResponseEntity = {
    HttpEntity(s"""{id:$id}""").withContentType(ContentType(MediaTypes.`application/json`))
  }

  def createUser(user: User): Future[Option[User]] = {
    dbActor ? CreateUser(user) flatMap {
      case res: Option[User] =>
        Future.successful(res)
    }
  }
}
