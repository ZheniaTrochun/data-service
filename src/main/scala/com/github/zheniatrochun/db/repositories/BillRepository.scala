package com.github.zheniatrochun.db.repositories

import java.sql.Date

import com.byteslounge.slickrepo.meta.Keyed
import com.byteslounge.slickrepo.repository.Repository
import com.byteslounge.slickrepo.scalaversion.JdbcProfile
import com.github.zheniatrochun.db.models.Bill
import slick.ast.BaseTypedType

class BillRepository(override val driver: JdbcProfile) extends Repository[Bill, Int](driver) {
  import driver.api._

  override type TableType = Bills

  override def pkType = implicitly[BaseTypedType[Int]]

  override def tableQuery = TableQuery[Bills]

  private class Bills(tag: Tag) extends Table[Bill](tag, "BILLS") with Keyed[Int] {
    def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
    def user = column[Int]("user")
    def date = column[Date]("date", O.SqlType("DATE"))
    def amount = column[Int]("amount")
    def currency = column[String]("currency")

    override def * = (id.?, user, date, amount, currency) <> ((Bill.apply _).tupled, Bill.unapply)
  }
}
