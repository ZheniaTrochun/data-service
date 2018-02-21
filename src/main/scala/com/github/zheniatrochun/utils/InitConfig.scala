package com.github.zheniatrochun.utils

import java.net.URI

import com.redis.RedisClient

object InitConfig extends Config {
  private val redisClient = new RedisClient(new URI(config.getString("redis.url")))

  def cacheInitialConfigs(): Map[String, String] = {
    if (!redisClient.connected)
      redisClient.connect

    val res = redisClient.hmget[String, String]("data-service-config")
      .getOrElse(Map.empty[String, String])

    redisClient.disconnect

    res
  }

  def createDummyConfig(): Unit = {
    if (!redisClient.connected)
      redisClient.connect

    val conf = Map("Sertificate" -> "123")
    val res = redisClient.hmset("data-service-config", conf)

    redisClient.disconnect

    res
  }
}
