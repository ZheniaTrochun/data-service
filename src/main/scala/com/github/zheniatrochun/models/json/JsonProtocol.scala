package com.github.zheniatrochun.models.json

import java.sql.Date

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.github.zheniatrochun.models.dto.{BillDto, WalletDto}
import com.github.zheniatrochun.models.{Bill, User, Wallet}
import org.joda.time.DateTime
import org.joda.time.format.{DateTimeFormatter, ISODateTimeFormat}
import spray.json.{DefaultJsonProtocol, DeserializationException, JsString, JsValue, RootJsonFormat}

object JsonProtocol extends DefaultJsonProtocol with SprayJsonSupport {

  implicit object DateJsonFormat extends RootJsonFormat[DateTime] {

    private val parserISO : DateTimeFormatter = ISODateTimeFormat.dateTimeNoMillis()

    override def write(obj: DateTime) = JsString(parserISO.print(obj))

    override def read(json: JsValue) : DateTime = json match {
      case JsString(s) => parserISO.parseDateTime(s)
      case _ => throw DeserializationException("Invalid date format: " + json)
    }
  }

  implicit object SqlDateJsonFormat extends RootJsonFormat[Date] {

    override def write(obj: Date) = JsString(obj.toString)
    override def read(json: JsValue) = json match {
      case JsString(s) => Date.valueOf(s)
      case _ => throw DeserializationException("Invalid date format: " + json)
    }

  }


  implicit val userFormat = jsonFormat3(User)
  implicit val billFormat = jsonFormat6(Bill)
  implicit val billDtoFormat = jsonFormat4(BillDto)
  implicit val walletFormat = jsonFormat4(Wallet)
  implicit val walletDtoFormat = jsonFormat2(WalletDto)
}
