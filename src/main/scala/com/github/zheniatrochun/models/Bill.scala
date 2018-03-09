package com.github.zheniatrochun.models

import java.sql.Date

import com.byteslounge.slickrepo.meta.Entity
import com.github.zheniatrochun.models.dto.BillDto

case class Bill(id: Option[Int], user: Int, date: Date, amount: Double, currency: String, tags: Option[String])
  extends Entity[Bill, Int] {

  override def withId(id: Int) = this.copy(id = Some(id))
}

class BillBuilder(date: Date, amount: Double, currency: String, tags: Option[String]) {
  private var id: Option[Int] = None
  private var user: Int = 0
  val self = this

  def withUser(user: Int): this.type = {
    this.user = user
    self
  }

  def withId(id: Option[Int]): this.type = {
    this.id = id
    self
  }

  def build(): Bill = {
    Bill(id, user, date, amount, currency, tags)
  }
}

object BillBuilder {
  def apply(billDto: BillDto): BillBuilder =
    new BillBuilder(billDto.date, billDto.amount, billDto.currency, billDto.tags)
}