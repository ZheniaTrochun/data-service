package com.github.zheniatrochun.actors


import akka.actor.{Actor, ActorSystem}
import akka.pattern.pipe
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import akka.stream.Materializer
import akka.stream.scaladsl.{Flow, Sink, Source}
import com.github.zheniatrochun.config.AppConfig
import com.github.zheniatrochun.models.requests.{AskRate, SendRequestToAuth, SendRequestToData, SendRequestToStatistics}
import org.slf4j.LoggerFactory

import scala.language.postfixOps
import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class HttpActor(implicit val system: ActorSystem, implicit val mat: Materializer)
  extends Actor with AppConfig {

  lazy val dataServiceApiConnectionFlow: Flow[HttpRequest, HttpResponse, Any] =
    Http().outgoingConnection(config.getString("services.data-service.host"))

  lazy val currencyApiConnectionFlow: Flow[HttpRequest, HttpResponse, Any] =
    Http().outgoingConnection(config.getString("currency-converter.api"))

  lazy val authServiceApiConnectionFlow: Flow[HttpRequest, HttpResponse, Any] =
    Http().outgoingConnection(config.getString("services.auth-service.host"))

  lazy val statsServiceApiConnectionFlow: Flow[HttpRequest, HttpResponse, Any] =
    Http().outgoingConnection(config.getString("services.statistics-service.host"))

  private val logger = LoggerFactory.getLogger(this.getClass)

  override def receive = {
    case SendRequestToAuth(request) =>
      logger.debug(s"Sending request to auth-service req = $request")
      pipe { runWithFlow(request, authServiceApiConnectionFlow) } to sender

    case SendRequestToData(request) =>
      logger.debug(s"Sending request to data-service req = $request")
      pipe { runWithFlow(request, dataServiceApiConnectionFlow) } to sender

    case SendRequestToStatistics(request) =>
      logger.debug(s"Sending request to statistics-service req = $request")
      pipe { runWithFlow(request, statsServiceApiConnectionFlow) } to sender

    case AskRate(request) =>
      logger.debug(s"Sending request to currency converter req = $request")
      pipe { runWithFlow(request, currencyApiConnectionFlow) } to sender


    case _ =>
      logger.error("Invalid request type!")
  }

  private def runWithFlow(request: HttpRequest, api: Flow[HttpRequest, HttpResponse, Any]): Future[HttpResponse] = {
    Source.single(request).via(api).runWith(Sink.head) map { resp =>
      logger.debug(s"Response for [${request.toString}] status = ${resp.status}, entity = ${resp.entity.toString}")
      resp
    }
  }
}
