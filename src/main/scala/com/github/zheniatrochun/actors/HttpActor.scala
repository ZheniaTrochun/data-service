package com.github.zheniatrochun.actors

import akka.actor.{Actor, ActorSystem}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import akka.stream.Materializer
import akka.stream.scaladsl.{Flow, Sink, Source}
import com.github.zheniatrochun.config.AppConfig
import com.github.zheniatrochun.models.requests.{SendRequestToAuth, SendRequestToData}

import scala.language.postfixOps
import scala.util.{Failure, Success}

class HttpActor(implicit val system: ActorSystem, implicit val mat: Materializer)
  extends Actor with AppConfig {

  lazy val dataServiceApiConnectionFlow: Flow[HttpRequest, HttpResponse, Any] =
    Http().outgoingConnection(config.getString("services.data-service.host"))

  lazy val authServiceApiConnectionFlow: Flow[HttpRequest, HttpResponse, Any] =
    Http().outgoingConnection(config.getString("services.auth-service.host"))

  override def receive = {
    case SendRequestToAuth(promise, request) =>
      Source.single(request).via(authServiceApiConnectionFlow).runWith(Sink.head) onComplete {
        case Success(resp: HttpResponse) =>
          promise.success(resp)

        case Failure(ex) =>
          promise.failure(ex)
      }

    case SendRequestToData(promise, request) =>
      Source.single(request).via(dataServiceApiConnectionFlow).runWith(Sink.head) onComplete {
        case Success(resp: HttpResponse) =>
          promise.success(resp)

        case Failure(ex) =>
          promise.failure(ex)
      }

  }
}
