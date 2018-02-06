package com.github.zheniatrochun.db.models

import com.byteslounge.slickrepo.meta.Entity

case class User(id: Int, name: String, email: String) extends Entity[User, Int] {
  override def withId(id: Int): User = this.copy(id = id)
}
