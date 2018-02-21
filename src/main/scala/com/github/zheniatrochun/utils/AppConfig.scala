package com.github.zheniatrochun.utils

import scala.language.postfixOps

trait AppConfig extends Config {
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
}
