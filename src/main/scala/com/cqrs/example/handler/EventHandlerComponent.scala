package com.cqrs.example.handler

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.event.LoggingReceive
import com.cqrs.example.ReadServiceLayer
import com.cqrs.example.handler.model.InsertBookToReadDb
import akka.pattern.pipe

trait EventHandlerComponent {

  this: ReadServiceLayer =>

  val eventHandler: ActorRef

  final class EventHandler extends Actor with ActorLogging {

    import context.dispatcher

    override def preStart(): Unit = {
      super.preStart()
      log.info(s"Starting ${EventHandler.name} actor")
    }

    override def postStop(): Unit = {
      super.postStop()
      log.info(s"Stopping ${EventHandler.name} actor")
    }

    def receive: Receive = LoggingReceive {
      case InsertBookToReadDb(bookDocument) =>
        bookReadService.insert(bookDocument) pipeTo sender; ()
    }
  }

  object EventHandler {

    val name: String = "cqrs-event-handler"

    def apply: Props = Props(new EventHandler)
  }
}
