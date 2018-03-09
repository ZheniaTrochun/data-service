package com.github.zheniatrochun.utils

import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.github.zheniatrochun.config.AppConfig
import com.github.zheniatrochun.models.json.JsonProtocol._
import org.slf4j.LoggerFactory
import spray.json._

import scala.concurrent.Future
import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContext.Implicits.global

trait RouteUtils extends AppConfig {

  val logger = LoggerFactory.getLogger(this.getClass)

  private class IdWriter extends JsonWriter[Int] {
    override def write(obj: Int): JsValue = s"""{"id":$obj}""".parseJson
  }

  implicit val idWriter: JsonWriter[Int] = new IdWriter()

  implicit class jsonFromOptionalFuture[T](f: Future[Option[T]]) {
    def toFutureJson(implicit writer: JsonWriter[T]): Future[Option[JsValue]] = {
      f.map(res => res.map(_.toJson))
    }
  }

  implicit class jsonFromFuture[T](f: Future[T]) {
    def toFutureJson(implicit writer: JsonWriter[T]): Future[Option[JsValue]] = {
      f.map(res => Some(res.toJson))
    }
  }

  implicit class JsonFromBoolean(f: Future[Boolean]) {
    def toFutureJson: Future[Option[JsValue]] = {
      f.map { res: Boolean =>
        if (res) Some(s"""{"success":"$res"}""".parseJson) else None
      }
    }
  }

  def completeWithFuture(f: => Future[Option[JsValue]]): Route = {
    onComplete(f) {

      case Success(res) =>
        res match {
          case Some(obj) =>
            val response = HttpResponse(StatusCodes.OK, entity = buildEntity(obj))
            complete(response)

          case None =>
            val response = HttpResponse(StatusCodes.BadRequest)
            complete(response)
        }

      case Failure(err) =>
        logger.error("Error occurred! ATTENTION!", err)
        val response = HttpResponse(StatusCodes.InternalServerError, entity = HttpEntity(err.getMessage))
        complete(response)
    }
  }

  def withSertificate(action: => Route): Route = {
    headerValueByName("Sertificate") { sertificate =>

      if (sertificate.isEmpty || sertificate != config.getString("Sertificate")) {
        reject()
      } else {
        action
      }
    }
  }

  private def buildEntity(obj: JsValue): ResponseEntity = {
    HttpEntity(obj.toString).withContentType(ContentType(MediaTypes.`application/json`))
  }
}
