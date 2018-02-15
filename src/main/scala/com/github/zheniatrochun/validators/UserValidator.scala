package com.github.zheniatrochun.validators

import com.github.zheniatrochun.models.User
import com.github.zheniatrochun.utils.UserValidatorUtils

object UserValidator extends UserValidatorUtils {
  implicit class UserValidatorImpl(user: User) {
    def isValid(): Boolean = {
      isEmailValid(user.email) && isUsernameValid(user.name)
    }
  }
}
