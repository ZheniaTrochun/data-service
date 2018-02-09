package com.github.zheniatrochun.utils

import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives.{complete, onComplete}
import akka.http.scaladsl.server.Route
import com.github.zheniatrochun.models.User
import com.github.zheniatrochun.models.json.JsonProtocol._
import spray.json._

import scala.concurrent.Future
import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContext.Implicits.global

trait RouteUtils {

  implicit val idWriter: JsonWriter[Int] = IdWriter

  implicit class jsonFromOptionalFuture[T](f: Future[Option[T]]) {
    def toFutureJson(implicit writer: JsonWriter[T]): Future[Option[JsValue]] = {
      f.map(res => res.map(_.toJson))
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
        val response = HttpResponse(StatusCodes.InternalServerError)
        complete(response)
    }
  }

  private def buildEntity(obj: JsValue): ResponseEntity = {
    HttpEntity(obj.toString).withContentType(ContentType(MediaTypes.`application/json`))
  }

  private object IdWriter extends JsonWriter[Int] {
    override def write(obj: Int): JsValue = s"""{id:$obj}""".toJson
  }
}
