package com.github.zheniatrochun.models.requests

import akka.http.scaladsl.model.{HttpRequest, HttpResponse}

import scala.concurrent.Promise

sealed trait SystemHttpRequest

case class SendRequestToAuth(promise: Promise[HttpResponse], request: HttpRequest) extends SystemHttpRequest

case class SendRequestToData(promise: Promise[HttpResponse], request: HttpRequest) extends SystemHttpRequest

case class SendRequestToStatistics(promise: Promise[HttpResponse], request: HttpRequest) extends SystemHttpRequest
