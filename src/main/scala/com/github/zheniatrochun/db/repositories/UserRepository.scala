package com.github.zheniatrochun.db.repositories

import com.byteslounge.slickrepo.meta.Keyed
import com.byteslounge.slickrepo.repository.Repository
import com.byteslounge.slickrepo.scalaversion.JdbcProfile
import com.github.zheniatrochun.db.models.User
import slick.ast.BaseTypedType
import slick.dbio.{DBIOAction, NoStream}

class UserRepository(override val driver: JdbcProfile) extends Repository[User, Int](driver) {

  import driver.api._

  override type TableType = Users
  override def pkType = implicitly[BaseTypedType[Int]]
  override def tableQuery = TableQuery[Users]


  def findOneByName(name: String) = {
    tableQuery.filter(_.name === name).result
  }

  def findOneByEmail(email: String) = {
    tableQuery.filter(_.email === email).result
  }

  def deleteById(id: Int) = {
    tableQuery.filter(_.id === id).delete
  }


  private class Users(tag: Tag) extends Table[User](tag, "USERS") with Keyed[Int] {
    def id = column[Int]("ID", O.PrimaryKey)
    def name = column[String]("name", O.SqlType("VARCHAR(20)"), O.Unique)
    def email = column[String]("email", O.SqlType("VARCHAR(30)"), O.Unique)

    override def * = (id, name, email) <> ((User.apply _).tupled, User.unapply)
  }
}
