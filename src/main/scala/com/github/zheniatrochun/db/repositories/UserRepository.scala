package com.github.zheniatrochun.db.repositories

import com.byteslounge.slickrepo.meta.Keyed
import com.byteslounge.slickrepo.repository.Repository
import com.byteslounge.slickrepo.scalaversion.JdbcProfile
import com.github.zheniatrochun.models.User
import slick.ast.BaseTypedType
import slick.sql.{FixedSqlAction, SqlAction}

class UserRepository(override val driver: JdbcProfile) extends Repository[User, Int](driver) {

  import driver.api._

  override type TableType = Users
  override def pkType = implicitly[BaseTypedType[Int]]
  override def tableQuery = TableQuery[Users]


  def setupSchema(): FixedSqlAction[Unit, NoStream, Effect.Schema] = {
    tableQuery.schema.create
  }

  def dropSchema(): FixedSqlAction[Unit, NoStream, Effect.Schema] = {
    tableQuery.schema.drop
  }

  def findOneByName(name: String): SqlAction[Option[User], NoStream, Effect.Read] = {
    tableQuery.filter(_.name === name).result.headOption
  }

  def findOneByEmail(email: String): SqlAction[Option[User], NoStream, Effect.Read] = {
    tableQuery.filter(_.email === email).result.headOption
  }

  def deleteById(id: Int): FixedSqlAction[Int, NoStream, Effect.Write] = {
    tableQuery.filter(_.id === id).delete
  }


  class Users(tag: Tag) extends Table[User](tag, "USERS") with Keyed[Int] {
    def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name", O.SqlType("VARCHAR(20)"), O.Unique)
    def email = column[String]("email", O.SqlType("VARCHAR(30)"), O.Unique)

    override def * = (id.?, name, email) <> ((User.apply _).tupled, User.unapply)
  }
}
