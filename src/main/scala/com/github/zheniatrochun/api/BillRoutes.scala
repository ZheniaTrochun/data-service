package com.github.zheniatrochun.api

import akka.http.scaladsl.server.Directives._
import com.github.zheniatrochun.models.{Bill, User}
import com.github.zheniatrochun.services.BillService
import com.github.zheniatrochun.utils.RouteUtils
import com.github.zheniatrochun.models.json.JsonProtocol._
import spray.json._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class BillRoutes(val billService: BillService) extends RouteUtils {
  val routes = {
    path("users") {
      (path("create") & post) {
        entity(as[Bill]) { bill =>
          completeWithFuture {
            idAsJson(billService.create(bill))
          }
        }
      } ~
        (path("update") & put) {
          entity(as[Bill]) { bill =>
            completeWithFuture {
              billAsJson(billService.update(bill))
            }
          }
        } ~
        (path("delete") & delete) {
          parameters('id.as[Int]) { id =>
            completeWithFuture {
              idAsJson(billService.delete(id))
            }
          }
        } ~
        (path("get") & get) {
          parameters('id.as[Int]) { id =>
            completeWithFuture {
              billAsJson(billService.getById(id))
            }
          }
        }
    }
  }

  private def billAsJson(f: Future[Option[Bill]]): Future[Option[JsValue]] = {
    f flatMap {
      case Some(bill) =>
        Future.successful(Some(bill.toJson))

      case None =>
        Future.successful(None)
    }
  }
}
