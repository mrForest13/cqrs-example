package com.cqrs.example.handler

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.event.LoggingReceive
import com.cqrs.example.handler.model.InsertBookToReadDbEvent
import akka.pattern.pipe
import com.cqrs.example.service.read.BookReadServiceComponent

trait EventHandlerComponent {

  this: BookReadServiceComponent =>

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
      case InsertBookToReadDbEvent(bookDocument) =>
        bookReadService.insert(bookDocument) pipeTo sender; ()
    }
  }

  object EventHandler {

    val name: String = "cqrs-event-handler"

    def apply: Props = Props(new EventHandler)
  }
}
