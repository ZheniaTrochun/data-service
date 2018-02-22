package com.github.zheniatrochun.models

import java.sql.Date

import com.byteslounge.slickrepo.meta.Entity

case class Bill(id: Option[Int], user: Int, date: Date, amount: Int, currency: String, tags: Option[String])
  extends Entity[Bill, Int] {

  override def withId(id: Int) = this.copy(id = Some(id))
}
