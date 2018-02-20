package com.github.zheniatrochun.api

import akka.http.scaladsl.server.Directives._
import com.github.zheniatrochun.services.AdminService
import com.github.zheniatrochun.utils.{InitConfig, RouteUtils}


class AdminRoutes(val adminService: AdminService)
                 (implicit val apiConfig: Map[String, String])
  extends RouteUtils {

  val routes = {
    pathPrefix("admin") {
      pathPrefix("config") {
        (path("create-config") & get) {
          InitConfig.createDummyConfig()
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
        } ~
        (path("create-table-user") & get) {
          adminService.createUserTable()
          complete("Ok")
        } ~
        (path("create-table-bill") & get) {
          adminService.createBillTable()
          complete("Ok")
        } ~
        (path("drop-table-user") & get) {
          adminService.dropUserTable()
          complete("Ok")
        } ~
        (path("drop-table-bill") & get) {
          adminService.dropBillTable()
          complete("Ok")
        }
      }
    }
  }
}
