package com.github.zheniatrochun.utils

import akka.actor.{ActorContext, ActorRef}

trait ActorMethodShortcuts {
  implicit class ForwardShortcut(actor: ActorRef) {
    def ~>(message: Any)(implicit context: ActorContext): Unit = actor forward message
  }
}
