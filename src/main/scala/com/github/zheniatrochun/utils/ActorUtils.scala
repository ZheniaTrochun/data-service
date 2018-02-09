package com.github.zheniatrochun.utils

import akka.actor.ActorRef
import com.github.zheniatrochun.exceptions.ActorFailureResponse

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

trait ActorUtils {

  implicit class ResponseSender(f: => Future[Any])(implicit val ec: ExecutionContext) {
    def sendResponseTo(actor: ActorRef): Unit = {
      f onComplete {
        case Success(s) =>
          actor ! s

        case Failure(e) =>
          actor ! ActorFailureResponse(e)
      }
    }
  }
}
