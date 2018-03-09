package com.github.zheniatrochun.models.dto

import java.sql.Date

case class BillDto(date: Date, amount: Double, currency: String, tags: Option[String])
