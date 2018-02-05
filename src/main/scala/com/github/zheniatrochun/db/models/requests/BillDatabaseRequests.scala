package com.github.zheniatrochun.db.models.requests

trait BillDatabaseRequest

case class FindBillById(id: Int) extends BillDatabaseRequest
case class FindBillByUser(user: Int) extends BillDatabaseRequest

case class DeleteBill(id: Int) extends BillDatabaseRequest

case class CreateBill() extends BillDatabaseRequest

case class UpdateBill() extends BillDatabaseRequest
