package com.github.zheniatrochun.services

import akka.actor.ActorRef
import akka.pattern.ask
import akka.util.Timeout
import com.github.zheniatrochun.models.Wallet
import com.github.zheniatrochun.models.requests._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait WalletService {
  def create(wallet: Wallet): Future[Option[Int]]
  def getById(id: Int): Future[Option[Wallet]]
  def update(wallet: Wallet): Future[Option[Wallet]]
  def delete(id: Int): Future[Boolean]
  def getAllByUser(user: Int): Future[Seq[Wallet]]
  def getAll(): Future[Seq[Wallet]]
}

class WalletServiceImpl(dbActor: ActorRef)
                       (implicit val timeout: Timeout) extends WalletService {

  override def create(wallet: Wallet): Future[Option[Int]] = {
    dbActor ? CreateWallet(wallet) flatMap {
      case Some(wallet: Wallet) =>
        Future.successful(wallet.id)

      case None =>
        Future.successful(None)
    }
  }

  override def getById(id: Int) = {
    dbActor ? FindWalletById(id) flatMap {
      case Some(wallet: Wallet) =>
        Future.successful(Some(wallet))

      case None =>
        Future.successful(None)
    }
  }

  override def update(wallet: Wallet) = {
    dbActor ? UpdateWallet(wallet) flatMap {
      case Some(wallet: Wallet) =>
        Future.successful(Some(wallet))

      case None =>
        Future.successful(None)
    }
  }

  override def delete(id: Int) = {
    dbActor ? DeleteWallet(id) flatMap {
      case num: Int if num != 0 =>
        Future.successful(true)

      case _ =>
        Future.successful(false)
    }
  }

  override def getAllByUser(user: Int) = {
    dbActor ? FindAllWalletsByUser(user) flatMap {
      case res: Seq[Wallet] =>
        Future.successful(res)

      case _ =>
        Future.failed(new RuntimeException())
    }
  }

  override def getAll() = {
    dbActor ? FindAllWallets flatMap {
      case res: Seq[Wallet] =>
        Future.successful(res)

      case _ =>
        Future.failed(new RuntimeException())
    }
  }
}
