package com.github.zheniatrochun.security

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import authentikat.jwt.JsonWebToken
import com.github.zheniatrochun.config.AppConfig
import io.jsonwebtoken.Jwts

trait JwtUtils extends AppConfig {

  private val secret = config.getString("api.security.secret")

  def extractUser(action: String => Route): Route = {
    headerValueByName("Authentication") { jwt =>
      getUserFromJwt(jwt) match {
        case Some(user) =>
          action(user)
        case None =>
          complete(StatusCodes.Unauthorized)
      }
    }
  }

  def validateJwt(action: => Route): Route = {
    headerValueByName("Authentication") { jwt =>
      validateJwtOrElse[Route](jwt)(complete(StatusCodes.Unauthorized)) {
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

  private def validateJwtOrElse[T](jwt: String)(default: T)(action: => T): T = {
    if (try {
          Jwts.parser.setSigningKey(secret).parseClaimsJws(jwt)
          true
        } catch {
          case _ => false
        }) {
      action
    } else {
      default
    }
  }

  private def checkUserOrElse[T](jwt: String, user: String)(default: T)(action: => T): T = {

    Option(Jwts.parser().setSigningKey(secret).parseClaimsJws(jwt).getBody.getSubject)
      .filter(u => u == user)
      .map(_ => action)
      .getOrElse(default)

  }

  private def getUserFromJwt(jwt: String): Option[String] = {
    Option(Jwts.parser().setSigningKey(secret).parseClaimsJws(jwt).getBody.getSubject)
  }

}
