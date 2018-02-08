package com.github.zheniatrochun.services

import com.github.zheniatrochun.models.Bill

import scala.concurrent.Future

trait BillService {
  def create(bill: Bill): Future[Option[Int]] = Future.successful(None)

  def update(bill: Bill): Future[Option[Bill]] = Future.successful(None)

  def delete(id: Int): Future[Option[Int]] = Future.successful(None)

  def getById(id: Int): Future[Option[Bill]] = Future.successful(None)

}
