package com.cqrs.write.handler

import akka.actor.ActorRef

trait EventHandlerComponent {

  val eventHandler: ActorRef
}
