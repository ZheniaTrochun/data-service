package com.github.zheniatrochun.api

import akka.http.scaladsl.server.Directives._

trait HealthRoutes {
  val healthRoutes =
    pathSingleSlash(complete("I'm alive! ALIVE!")) ~ path("ping")(complete("PONG!"))
}
