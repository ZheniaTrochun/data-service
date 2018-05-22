package com.github.zheniatrochun.models.requests

import akka.http.scaladsl.model.{HttpRequest, HttpResponse}

import scala.concurrent.Promise

sealed trait SystemHttpRequest

case class SendRequestToAuth(request: HttpRequest) extends SystemHttpRequest

case class SendRequestToData(request: HttpRequest) extends SystemHttpRequest

case class SendRequestToStatistics(request: HttpRequest) extends SystemHttpRequest

case class AskRate(request: HttpRequest) extends SystemHttpRequest
