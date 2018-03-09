package com.github.zheniatrochun.api

import akka.http.scaladsl.server.Directives._
import com.github.zheniatrochun.models.{Bill, BillBuilder}
import com.github.zheniatrochun.models.dto.BillDto
import com.github.zheniatrochun.services.BillService
import com.github.zheniatrochun.utils.RouteUtils
import com.github.zheniatrochun.models.json.JsonProtocol._
import com.github.zheniatrochun.security.{JwtUtils, JwtUtils}
import spray.json._

import scala.language.postfixOps


class BillRoutes(val billService: BillService)
  extends RouteUtils with JwtUtils {

  val routes = {
    path("bills") {
      post {
        entity(as[BillDto]) { dto =>
          validateJwt {
            extractUser { user =>
              completeWithFuture {
                billService.create(dto, user).toFutureJson(idWriter)
              }
            }
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
        } ~
        path("/all") {
          completeWithFuture {
            billService.getAll().toFutureJson
          }
        }
      }
    }
  }
}
