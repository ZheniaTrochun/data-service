package com.github.zheniatrochun.models.requests

import com.github.zheniatrochun.models.Bill

trait MqRequest

case class PublishBill(bill: Bill) extends MqRequest
