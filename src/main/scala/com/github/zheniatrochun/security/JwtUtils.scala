package com.github.zheniatrochun.security

import authentikat.jwt.JsonWebToken
import com.github.zheniatrochun.config.AppConfig

trait JwtUtils extends AppConfig {
  private val secret = config.getString("api.security.secret")

  def validateJwtOrElse[T](jwt: String)(default: T)(action: => T): T = {
    if (JsonWebToken.validate(jwt, secret)) {
      action
    } else {
      default
    }
  }

  def checkUserOrElse[T](jwt: String, user: String)(default: T)(action: => T): T = {
    jwt match {
      case JsonWebToken(_, claims, _) =>
        if (claims.asSimpleMap.get.getOrElse("user", "") == user) {
          action
        } else {
          default
        }
      case _ =>
        default
    }
  }
}
