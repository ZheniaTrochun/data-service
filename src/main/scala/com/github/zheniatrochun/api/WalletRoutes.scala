package com.github.zheniatrochun.api

import akka.http.scaladsl.server.Directives._
import com.github.zheniatrochun.models.Wallet
import com.github.zheniatrochun.services.WalletService
import com.github.zheniatrochun.utils.RouteUtils
import com.github.zheniatrochun.models.json.JsonProtocol._
import spray.json._

import scala.language.postfixOps

class WalletRoutes(val walletService: WalletService) extends RouteUtils {

  val routes =
    pathPrefix("wallets") {
      post {
        entity(as[Wallet]) { wallet: Wallet =>
          completeWithFuture {
            walletService.create(wallet) toFutureJson
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
