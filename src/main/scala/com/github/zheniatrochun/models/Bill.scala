package com.github.zheniatrochun.models

import java.sql.Date

import com.byteslounge.slickrepo.meta.Entity
import com.github.zheniatrochun.models.dto.BillDto

case class Bill(id: Option[Int], user: Int, date: Date, amount: Double, currency: String, tags: String, wallet: Option[Int])
  extends Entity[Bill, Int] {

  override def withId(id: Int) = this.copy(id = Some(id))
}

class BillBuilder(date: Date, amount: Double, currency: String, tags: String, wallet: Option[Int]) {
  var id: Option[Int] = None
  var user: Int = 0

  def withUser(user: Int): this.type = {
    this.user = user
    this
  }

  def withId(id: Option[Int]): this.type = {
    this.id = id
    this
  }

  def build(): Bill = {
    Bill(id, user, date, amount, currency, tags, wallet)
  }
}

object BillBuilder {
  def apply(billDto: BillDto): BillBuilder =
    new BillBuilder(billDto.date, billDto.amount, billDto.currency, billDto.tags.getOrElse("-"), billDto.wallet)
}