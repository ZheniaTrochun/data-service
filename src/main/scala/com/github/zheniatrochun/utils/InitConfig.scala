package com.github.zheniatrochun.utils

import java.net.URI

import com.redis.RedisClient
import org.slf4j.LoggerFactory

object InitConfig extends Config {
  private val redisClient = new RedisClient(new URI(config.getString("redis.url")))

  private val logger = LoggerFactory.getLogger(this.getClass)

  def cacheInitialConfigs(): Map[String, String] = {
//    if (!redisClient.connected)
//      redisClient.connect

    val res = redisClient.hmget[String, String]("data-service-config", "*")
      .getOrElse(Map.empty[String, String])

    res.foreach(p => logger.info(s"TEST CONFIG MAP - $p"))

//    redisClient.disconnect

    res
  }

  def createDummyConfig(): Unit = {
//    if (!redisClient.connected)
//      redisClient.connect

    val conf = Map("Sertificate" -> "123")
    redisClient.hmset("data-service-config", conf)

//    redisClient.disconnect
  }
}
