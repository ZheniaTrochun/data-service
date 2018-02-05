package com.github.zheniatrochun

import akka.http.scaladsl.server.Directives._
import com.github.zheniatrochun.api.UsersApi

trait Routes extends UsersApi {
  val routes = pathPrefix("v1") {
    usersRoutes
  } ~ path("")(getFromResource("public/index.html"))
}
