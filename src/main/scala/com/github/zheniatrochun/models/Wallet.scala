package com.github.zheniatrochun.models

import com.byteslounge.slickrepo.meta.Entity

case class Wallet(id: Option[Int], name: String, user: Int, amount: Double) extends Entity[Wallet, Int] {
  override def withId(id: Int): Wallet = this.copy(id = Some(id))
}
