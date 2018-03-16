package com.github.zheniatrochun.api

import akka.http.scaladsl.server.Directives._
import com.github.zheniatrochun.models.Wallet
import com.github.zheniatrochun.models.dto.WalletDto
import com.github.zheniatrochun.services.WalletService
import com.github.zheniatrochun.utils.RouteUtils
import com.github.zheniatrochun.models.json.JsonProtocol._
import com.github.zheniatrochun.security.JwtUtils
import spray.json._

import scala.language.postfixOps

class WalletRoutes(val walletService: WalletService) extends RouteUtils with JwtUtils {

  val routes =
    pathPrefix("wallets") {
      post {
        entity(as[WalletDto]) { walletDto: WalletDto =>
          validateJwt {
            extractUser { user: String =>
              completeWithFuture {
                walletService.create(walletDto, user) toFutureJson
              }
            }
          }
        }
      } ~
      get {
        path("all") {
          completeWithFuture {
            walletService.getAll() toFutureJson
          }
        }
      }
    }
}
