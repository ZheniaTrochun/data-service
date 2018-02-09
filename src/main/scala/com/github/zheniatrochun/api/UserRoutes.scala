package com.github.zheniatrochun.api

import akka.http.scaladsl.server.Directives._
import com.github.zheniatrochun.models.User
import com.github.zheniatrochun.services.UserService
import com.github.zheniatrochun.utils.RouteUtils
import com.github.zheniatrochun.models.json.JsonProtocol._
import spray.json._



class UserRoutes(val userService: UserService) extends RouteUtils {

  val routes = {
    path("users") {
      (path("create") & post) {
        entity(as[User]) { user =>
          completeWithFuture {
            userService.create(user).toFutureJson
          }
        }
      } ~
      (path("update") & put) {
        entity(as[User]) { user =>
          completeWithFuture {
            userService.update(user).toFutureJson
          }
        }
      } ~
      (path("delete") & delete) {
        parameters('id.as[Int]) { id =>
          completeWithFuture {
            userService.delete(id).toFutureJson
          }
        }
      } ~
      (path("get") & get) {
        parameters('id.as[Int]) { id =>
          completeWithFuture {
            userService.getById(id).toFutureJson
          }
        } ~
        parameters('name.as[String]) { name =>
          completeWithFuture {
            userService.getByName(name).toFutureJson
          }
        } ~
        parameters('email.as[String]) { email =>
          completeWithFuture {
            userService.getByEmail(email).toFutureJson
          }
        }
      }
    }
  }
}
