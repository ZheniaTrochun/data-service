package com.github.zheniatrochun.utils

import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives.{complete, onComplete}
import akka.http.scaladsl.server.Route
import com.github.zheniatrochun.models.json.JsonProtocol._
import spray.json._

import scala.concurrent.Future
import scala.util.{Failure, Success}

trait RouteUtils[A] {
  def completeWithFuture(f: => Future[Option[Any]]): Route = {
    onComplete(f) {

      case Success(res) =>
        res match {
          case Some(obj: A) =>
            val response = HttpResponse(StatusCodes.OK, entity = buildEntity(obj))
            complete(response)

          case Some(id: Int) =>
            val response = HttpResponse(StatusCodes.OK, entity = buildEntity(id))
            complete(response)

          case None =>
            val response = HttpResponse(StatusCodes.BadRequest)
            complete(response)

          case _ =>
            val response = HttpResponse(StatusCodes.InternalServerError)
            complete(response)
        }

      case Failure(err) =>
        val response = HttpResponse(StatusCodes.InternalServerError)
        complete(response)
    }
  }

  def buildEntity(obj: A): ResponseEntity = {
    HttpEntity(obj.toJson.toString).withContentType(ContentType(MediaTypes.`application/json`))
  }

  def buildEntity(id: Int): ResponseEntity = {
    HttpEntity(s"""{id:$id}""").withContentType(ContentType(MediaTypes.`application/json`))
  }
}
