package com.github.zheniatrochun.models

import com.byteslounge.slickrepo.meta.Entity

case class User(id: Option[Int], name: String, email: String) extends Entity[User, Int] {
  override def withId(id: Int): User = this.copy(id = Some(id))
}
