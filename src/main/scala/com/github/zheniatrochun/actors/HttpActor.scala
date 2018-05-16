package com.github.zheniatrochun.actors

import akka.actor.{Actor, ActorSystem}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import akka.stream.Materializer
import akka.stream.scaladsl.{Flow, Sink, Source}
import com.github.zheniatrochun.config.AppConfig
import com.github.zheniatrochun.models.requests.{SendRequestToAuth, SendRequestToData, SendRequestToStatistics}
import org.slf4j.LoggerFactory

import scala.language.postfixOps
import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContext.Implicits.global

class HttpActor(implicit val system: ActorSystem, implicit val mat: Materializer)
  extends Actor with AppConfig {

  lazy val dataServiceApiConnectionFlow: Flow[HttpRequest, HttpResponse, Any] =
    Http().outgoingConnection(config.getString("services.data-service.host"))

  lazy val authServiceApiConnectionFlow: Flow[HttpRequest, HttpResponse, Any] =
    Http().outgoingConnection(config.getString("services.auth-service.host"))

  lazy val statsServiceApiConnectionFlow: Flow[HttpRequest, HttpResponse, Any] =
    Http().outgoingConnection(config.getString("services.statistics-service.host"))

  private val logger = LoggerFactory.getLogger(this.getClass)

  override def receive = {
    case SendRequestToAuth(promise, request) =>
      logger.debug(s"Sending request to auth-service req = $request")
      Source.single(request).via(authServiceApiConnectionFlow).runWith(Sink.head) onComplete {
        case Success(resp: HttpResponse) =>
          logger.debug(s"Auth responded success resp = $resp")
          promise.success(resp)

        case Failure(ex) =>
          logger.warn(s"Auth response failed with ex = $ex")
          promise.failure(ex)
      }

    case SendRequestToData(promise, request) =>
      logger.debug(s"Sending request to data-service req = $request")
      Source.single(request).via(dataServiceApiConnectionFlow).runWith(Sink.head) onComplete {
        case Success(resp: HttpResponse) =>
          logger.debug(s"Data responded success resp = $resp")
          promise.success(resp)

        case Failure(ex) =>
          logger.warn(s"Data response failed with ex = $ex")
          promise.failure(ex)
      }

    case SendRequestToStatistics(promise, request) =>
      logger.debug(s"Sending request to statistics-service req = $request")
      Source.single(request).via(statsServiceApiConnectionFlow).runWith(Sink.head) onComplete {
        case Success(resp: HttpResponse) =>
          logger.debug(s"statistics responded success resp = $resp")
          promise.success(resp)

        case Failure(ex) =>
          logger.warn(s"statistics response failed with ex = $ex")
          promise.failure(ex)
      }

    case _ =>
      logger.error("Invalid request type!")
  }
}
