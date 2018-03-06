package com.github.zheniatrochun.security

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import com.github.zheniatrochun.config.AppConfig

trait JwtChecker extends JwtUtils with AppConfig {

  def validateJwt(action: => Route): Route = {
    headerValueByName("Authentication") { jwt =>
      validateJwtOrElse[Route](jwt, complete(StatusCodes.Unauthorized)) {
        action
      }
    }
  }

  def validateJwtAndCheckUser(user: String)(action: => Route): Route = {
    headerValueByName("Authentication") { jwt =>
      validateJwtOrElse[Route](jwt)(complete(StatusCodes.Unauthorized)) {
        checkUserOrElse[Route](jwt, user)(complete(StatusCodes.Forbidden)) {
          action
        }
      }
    }
  }
}
