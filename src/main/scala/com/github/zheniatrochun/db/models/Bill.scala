package com.github.zheniatrochun.db.models

import java.sql.Date

case class Bill(id: Option[Int], user: Int, date: Date, amount: Int, currency: String)
