package com.github.zheniatrochun.db.models.requests

import com.github.zheniatrochun.db.models.User

trait UserDatabaseRequest

case class FindUserById(id: Int) extends UserDatabaseRequest
case class FindUserByName(name: String) extends UserDatabaseRequest
case class FindUserByEmail(email: String) extends UserDatabaseRequest

case class DeleteUser(id: Int) extends UserDatabaseRequest

case class CreateUser(user: User) extends UserDatabaseRequest

case class UpdateUser(user: User) extends UserDatabaseRequest