package com.github.zheniatrochun.api

import akka.http.scaladsl.server.Directives._
import com.github.zheniatrochun.utils.BearerTokenGenerator

trait UsersApi extends BearerTokenGenerator {
  val usersRoutes =
    (path("users" / "authentication") & post) {
      complete(generateSHAToken("InstagramPicsFilter"))
    }
}
