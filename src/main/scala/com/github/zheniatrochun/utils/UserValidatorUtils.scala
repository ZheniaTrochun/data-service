package com.github.zheniatrochun.utils

import scala.util.matching.Regex

trait UserValidatorUtils {
  final val USERNAME_REGEX: Regex = "^[a-zA-Z0-9]+([_ -]?[a-zA-Z0-9])*$".r
  final val EMAIL_REGEX: Regex = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])".r

  def isUsernameValid(name: String): Boolean = {
    name match {
      case USERNAME_REGEX() => true
      case _ => false
    }
  }

  def isEmailValid(email: String): Boolean = {
    email match {
      case EMAIL_REGEX() => true
      case _ => false
    }
  }
}
