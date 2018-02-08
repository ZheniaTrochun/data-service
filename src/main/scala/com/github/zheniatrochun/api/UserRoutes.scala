package com.github.zheniatrochun.api

import akka.http.scaladsl.server.Directives._
import com.github.zheniatrochun.models.User
import com.github.zheniatrochun.services.UserService
import com.github.zheniatrochun.utils.RouteUtils


class UserRoutes(val userService: UserService) extends RouteUtils[User] {

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

}
