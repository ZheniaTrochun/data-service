package com.github.zheniatrochun.api

import akka.http.scaladsl.server.Directives._
import com.github.zheniatrochun.models.User
import com.github.zheniatrochun.services.UserService
import com.github.zheniatrochun.utils.RouteUtils
import com.github.zheniatrochun.models.json.JsonProtocol._
import spray.json._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global


class UserRoutes(val userService: UserService) extends RouteUtils {

  val routes = {
    path("users") {
      (path("create") & post) {
        entity(as[User]) { user =>
          completeWithFuture {
            idAsJson(userService.create(user))
          }
        }
      } ~
      (path("update") & put) {
        entity(as[User]) { user =>
          completeWithFuture {
            userAsJson(userService.update(user))
          }
        }
      } ~
      (path("delete") & delete) {
        parameters('id.as[Int]) { id =>
          completeWithFuture {
            idAsJson(userService.delete(id))
          }
        }
      } ~
      (path("get") & get) {
        parameters('id.as[Int]) { id =>
          completeWithFuture {
            userAsJson(userService.getById(id))
          }
        } ~
        parameters('name.as[String]) { name =>
          completeWithFuture {
            userAsJson(userService.getByName(name))
          }
        } ~
        parameters('email.as[String]) { email =>
          completeWithFuture {
            userAsJson(userService.getByEmail(email))
          }
        }
      }
    }
  }

  private def userAsJson(f: Future[Option[User]]): Future[Option[JsValue]] = {
    f flatMap {
      case Some(user) =>
        Future.successful(Some(user.toJson))

      case None =>
        Future.successful(None)
    }
  }
}
