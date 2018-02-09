package com.github.zheniatrochun.api

import akka.http.scaladsl.server.Directives._
import com.github.zheniatrochun.models.Bill
import com.github.zheniatrochun.services.BillService
import com.github.zheniatrochun.utils.RouteUtils
import com.github.zheniatrochun.models.json.JsonProtocol._
import spray.json._


class BillRoutes(val billService: BillService) extends RouteUtils {
  val routes = {
    path("users") {
      (path("create") & post) {
        entity(as[Bill]) { bill =>
          completeWithFuture {
            billService.create(bill).toFutureJson
          }
        }
      } ~
        (path("update") & put) {
          entity(as[Bill]) { bill =>
            completeWithFuture {
              billService.update(bill).toFutureJson
            }
          }
        } ~
        (path("delete") & delete) {
          parameters('id.as[Int]) { id =>
            completeWithFuture {
              billService.delete(id).toFutureJson
            }
          }
        } ~
        (path("get") & get) {
          parameters('id.as[Int]) { id =>
            completeWithFuture {
              billService.getById(id).toFutureJson
            }
          }
        }
    }
  }
}
