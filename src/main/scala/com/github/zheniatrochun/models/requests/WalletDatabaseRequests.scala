package com.github.zheniatrochun.models.requests

import com.github.zheniatrochun.models.Wallet

trait WalletDatabaseRequest

case class CreateWallet(wallet: Wallet) extends WalletDatabaseRequest
case class FindWalletById(id: Int) extends WalletDatabaseRequest
case class UpdateWallet(wallet: Wallet) extends WalletDatabaseRequest
case class DeleteWallet(id: Int) extends WalletDatabaseRequest
case class FindAllWalletsByUser(user: Int) extends WalletDatabaseRequest
case object FindAllWallets extends WalletDatabaseRequest
