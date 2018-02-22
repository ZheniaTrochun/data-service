package com.github.zheniatrochun.utils

import com.typesafe.config.ConfigFactory

import scala.language.postfixOps

trait AppConfig {
  val config = new PrivateConfigs()
  private val httpConfig = config.getConfig("http")

  val httpInterface = httpConfig.getString("interface")
  val httpPort = httpConfig.getInt("port")
}

class PrivateConfigs {
  private val config = ConfigFactory.load()
  private var configCache: Map[String, String] = InitConfig.cacheInitialConfigs()

  def getString(key: String): String = {
    configCache.getOrElse(key, config.getString(key))
  }

  def getInt(key: String): Int = {
    configCache.getOrElse(key, config.getString(key)) toInt
  }

  def update(): Unit = {
    configCache = InitConfig.cacheInitialConfigs()
  }

  def getConfig(key: String) = {
    config.getConfig(key)
  }
}
