package com.github.zheniatrochun.api

import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.github.zheniatrochun.models.User
//import com.github.zheniatrochun.models.json.JsonProtocol
import com.github.zheniatrochun.models.json.JsonProtocol._
import spray.json._
import com.github.zheniatrochun.services.UserService

import scala.concurrent.Future
import scala.util.{Failure, Success}


class UserRoutes(val userService: UserService) {

  val routes = {
    path("users") {
      (path("create") & post) {
        entity(as[User]) { user =>
          completeWithFuture(userService.create(user))
        }
      } ~
      (path("update") & put) {
        entity(as[User]) { user =>
          completeWithFuture(userService.update(user))
        }
      } ~
      (path("delete") & delete) {
        parameters('id.as[Int]) { id =>
          completeWithFuture(userService.delete(id))
        }
      } ~
      (path("get") & get) {
        parameters('id.as[Int]) { id =>
          completeWithFuture(userService.getById(id))
        } ~
        parameters('name.as[String]) { name =>
          completeWithFuture(userService.getByName(name))
        } ~
        parameters('email.as[String]) { email =>
          completeWithFuture(userService.getByEmail(email))
        }
      }
    }
  }


  def completeWithFuture(f: => Future[Option[Any]]): Route = {
    onComplete(f) {

      case Success(success) =>
        success match {
          case res: Option[_] =>
            res match {
              case Some(user: User) =>
                val response = HttpResponse(StatusCodes.OK, entity = buildEntity(user))
                complete(response)

              case Some(id: Int) =>
                val response = HttpResponse(StatusCodes.OK, entity = buildEntity(id))
                complete(response)

              case None =>
                val response = HttpResponse(StatusCodes.BadRequest)
                complete(response)

              case _ =>
                val response = HttpResponse(StatusCodes.InternalServerError)
                complete(response)
            }

          case _ =>
            val response = HttpResponse(StatusCodes.InternalServerError)
            complete(response)
        }


      case Failure(err) =>
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
}
