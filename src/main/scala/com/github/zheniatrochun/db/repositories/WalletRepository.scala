package com.github.zheniatrochun.db.repositories

import com.byteslounge.slickrepo.meta.Keyed
import com.byteslounge.slickrepo.repository.Repository
import com.github.zheniatrochun.models.Wallet
import slick.ast.BaseTypedType
import slick.jdbc.JdbcProfile
import slick.sql.{FixedSqlAction, FixedSqlStreamingAction}

class WalletRepository(profile: JdbcProfile) extends Repository[Wallet, Int](profile) {

  import driver.api._

  override type TableType = Wallets

  override def pkType = implicitly[BaseTypedType[Int]]

  override def tableQuery = TableQuery[Wallets]



  def setupSchema() = tableQuery.schema.create

  def dropSchema() = tableQuery.schema.drop


  def deleteById(id: Int): FixedSqlAction[Int, NoStream, Effect.Write] = {
    tableQuery.filter(_.id === id).delete
  }

  def findAllByUser(user: Int): FixedSqlStreamingAction[Seq[Wallet], Wallet, Effect.Read] = {
    tableQuery.filter(_.user === user).result
  }

  class Wallets(tag: Tag) extends Table[Wallet](tag, "WALLETS") with Keyed[Int] {
    override def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
    def name = column[String]("Name")
    def user = column[Int]("User")
    def balance = column[Double]("Balance")

    override def * = (id.?, name, user, balance)<>((Wallet.apply _).tupled, Wallet.unapply)
  }
}
