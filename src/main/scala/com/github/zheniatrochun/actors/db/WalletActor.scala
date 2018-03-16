package com.github.zheniatrochun.actors.db

import akka.actor.Actor
import akka.pattern.{ask, pipe}
import akka.util.Timeout
import com.github.zheniatrochun.db.repositories.WalletRepository
import com.github.zheniatrochun.models.{User, WalletBuilder}
import com.github.zheniatrochun.models.requests._
import org.slf4j.LoggerFactory
import slick.jdbc.JdbcProfile

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


class WalletActor(val db: JdbcProfile#Backend#Database, val walletRepository: WalletRepository)
                 (implicit val timeout: Timeout) extends Actor {

  private val logger = LoggerFactory.getLogger(this.getClass)

  override def receive = {
    case CreateWallet(walletDto, username) =>
      logger.info(s"Received request: CreateWallet($walletDto, $username)")
      context.parent ? FindUserByName(username) foreach  {
        case Some(user: User) =>
          val wallet = WalletBuilder(walletDto).withUser(user.id.get).build()
          pipe(db.run(walletRepository.save(wallet))) to sender

        case None =>
          pipe(Future.successful(None)) to sender
      }
      context.stop(self)

    case FindWalletById(id) =>
      logger.info(s"Received request: FindWalletById($id)")
      pipe(db.run(walletRepository.findOne(id))) to sender
      context.stop(self)

    case UpdateWallet(wallet) =>
      logger.info(s"Received request: UpdateWallet($wallet)")
      pipe(db.run(walletRepository.update(wallet))) to sender
      context.stop(self)

    case DeleteWallet(wallet) =>
      logger.info(s"Received request: DeleteWallet($wallet)")
      pipe(db.run(walletRepository.deleteById(wallet))) to sender
      context.stop(self)

    case FindAllWalletsByUser(user) =>
      logger.info(s"Received request: FindAllWalletsByUser($user)")
      pipe(db.run(walletRepository.findAllByUser(user))) to sender
      context.stop(self)

    case FindAllWallets =>
      logger.info(s"Received request: FindAllWallets")
      pipe(db.run(walletRepository.findAll())) to sender
      context.stop(self)


    case CreateSchema =>
      logger.info(s"Received request: CreateSchema")
      db.run { walletRepository.setupSchema() }
      context.stop(self)

    case DropSchema =>
      logger.info(s"Received request: DropSchema")
      db.run { walletRepository.dropSchema() }
      context.stop(self)


    case _ =>

  }
}
