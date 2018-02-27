package com.github.zheniatrochun.api

import akka.http.scaladsl.server.Directives._
import com.github.zheniatrochun.config._
import com.github.zheniatrochun.services.AdminService
import com.github.zheniatrochun.utils.RouteUtils


class AdminRoutes(val adminService: AdminService)
  extends RouteUtils with AppConfig {

  val routes = {
    pathPrefix("admin") {
      pathPrefix("config") {
        (path("create-dummy") & get) {
          config.createDummyConfig()
          complete("Ok")
        } ~
        (path("update") & get) {
          config.update()
          complete("Ok")
        }
      } ~
      pathPrefix("db") {
        (path("create-tables") & get) {
          adminService.createTables()
          complete("Ok")
        } ~
        (path("drop-tables") & get) {
          adminService.dropTables()
          complete("Ok")
        }
      }
    }
  }
}
