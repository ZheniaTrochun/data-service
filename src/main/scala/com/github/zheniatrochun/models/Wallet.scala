package com.github.zheniatrochun.models

import com.byteslounge.slickrepo.meta.Entity
import com.github.zheniatrochun.models.dto.WalletDto

case class Wallet(id: Option[Int], name: String, user: Int, amount: Double) extends Entity[Wallet, Int] {
  override def withId(id: Int): Wallet = this.copy(id = Some(id))
}

case class WalletBuilder(name: String, amount: Double) {
  var user = 0

  def withUser(user: Int): this.type = {
    this.user = user
    this
  }

  def build(): Wallet = Wallet(None, name, user, amount)
}

object WalletBuilder {
  def apply(walletDto: WalletDto): WalletBuilder =
    new WalletBuilder(walletDto.name, walletDto.amount.getOrElse(0))
}