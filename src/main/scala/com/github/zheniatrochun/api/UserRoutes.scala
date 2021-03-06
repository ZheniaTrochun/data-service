package com.github.zheniatrochun.api

import akka.http.scaladsl.server.Directives._
import com.github.zheniatrochun.models.User
import com.github.zheniatrochun.services.UserService
import com.github.zheniatrochun.utils.RouteUtils
import com.github.zheniatrochun.models.json.JsonProtocol._
import com.github.zheniatrochun.security.JwtUtils
import spray.json._



class UserRoutes(val userService: UserService)
  extends RouteUtils with JwtUtils {

  val routes = {
    pathPrefix("users") {
      post {
        entity(as[User]) { user =>
          completeWithFuture {
            userService.create(user).toFutureJson(idWriter)
          }
        }
      } ~
      put {
        entity(as[User]) { user =>
          validateJwtAndCheckUser(user.name) {
            completeWithFuture {
              userService.update(user).toFutureJson
            }
          }
        }
      } ~
      delete {
        parameters('name) { name =>
          validateJwtAndCheckUser(name) {
            completeWithFuture {
              userService.deleteByName(name).toFutureJson
            }
          }
        }
      } ~
      get {
        path("all") {
          withSertificate {
            completeWithFuture {
              userService.getAll().toFutureJson
            }
          }
        } ~
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
