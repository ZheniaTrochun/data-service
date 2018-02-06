package com.github.zheniatrochun.db.models.requests

import com.github.zheniatrochun.db.models.Bill

trait BillDatabaseRequest

case class FindBillById(id: Int) extends BillDatabaseRequest
case class FindAllBillsByUser(user: Int) extends BillDatabaseRequest
case class FindAllBillsByUserPage(user: Int, page: Int) extends BillDatabaseRequest

case class DeleteBill(id: Int) extends BillDatabaseRequest

case class CreateBill(bill: Bill) extends BillDatabaseRequest

case class UpdateBill(bill: Bill) extends BillDatabaseRequest
