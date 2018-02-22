package com.github.zheniatrochun.api

import akka.http.scaladsl.server.Directives._
import com.github.zheniatrochun.models.Bill
import com.github.zheniatrochun.services.BillService
import com.github.zheniatrochun.utils.RouteUtils
import com.github.zheniatrochun.models.json.JsonProtocol._
import spray.json._

import scala.language.postfixOps


class BillRoutes(val billService: BillService)
  extends RouteUtils {

  val routes = {
    path("bills") {
      post {
        entity(as[Bill]) { bill =>
          completeWithFuture {
            billService.create(bill).toFutureJson(idWriter)
          }
        }
      } ~
        put {
          entity(as[Bill]) { bill =>
            completeWithFuture {
              billService.update(bill).toFutureJson
            }
          }
        } ~
        delete {
          parameters('id.as[Int]) { id =>
            completeWithFuture {
              billService.delete(id).toFutureJson(idWriter)
            }
          }
        } ~
        get {
          parameters('id.as[Int]) { id =>
            completeWithFuture {
              billService.getById(id).toFutureJson
            }
          } ~
          headerValueByName("user") { user =>
            completeWithFuture {
              billService.getAllByUser(user toInt).toFutureJson
            }
          }
        }
    }
  }
}
