package com.github.zheniatrochun.actors

import akka.actor.{Actor, Terminated}
import com.github.zheniatrochun.config.AppConfig
import com.github.zheniatrochun.models.Bill
import com.github.zheniatrochun.models.json.JsonProtocol._
import com.github.zheniatrochun.models.requests.PublishBill
import com.rabbitmq.client.ConnectionFactory
import org.slf4j.LoggerFactory
import spray.json._

class MessageQueueActor extends Actor with AppConfig {

  private val exchangeName = config.getString("rabbitmq.exchange.name")
  private val exchangeType = config.getString("rabbitmq.exchange.type")

  private val factory = new ConnectionFactory()
  factory.setUri(config.getString("rabbitmq.url"))

  private val connection = factory.newConnection()
  private val channel = connection.createChannel()

  channel.exchangeDeclare(exchangeName, exchangeType)

  private val logger = LoggerFactory.getLogger(this.getClass)

  override def receive = {
    case PublishBill(bill) =>
      logger.debug(s"Received bill for publishing to RabbitMQ bill = $bill")
      channel.basicPublish(exchangeName, "", null, bill.toJson.toString.getBytes())
      logger.debug("Publish successful! \\o/")

    case Terminated =>
      channel.close()
      connection.close()
  }


}
