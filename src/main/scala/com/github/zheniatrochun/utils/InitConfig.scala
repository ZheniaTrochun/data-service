package com.github.zheniatrochun.utils

import java.net.URI

import com.redis.RedisClient
import com.typesafe.config.ConfigFactory

trait InitConfig {
  def cacheInitialConfigs(): Map[String, String]

  def createDummyConfig(): Unit
}

// bridge for Redis
object InitConfig extends InitConfig {
  private val config = ConfigFactory.load()
  private val redisClient = new RedisClient(new URI(config.getString("redis.url")))

  def cacheInitialConfigs(): Map[String, String] = {
    redisClient.hgetall1[String, String]("data-service-config")
      .getOrElse(Map.empty[String, String])
  }

  def createDummyConfig(): Unit = {
    val conf = Map("Sertificate" -> "123")
    redisClient.hmset("data-service-config", conf)
  }
}
