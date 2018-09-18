package com.cqrs.read.handler

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.event.LoggingReceive
import akka.pattern.pipe
import com.cqrs.common.event.AddNewBook
import com.cqrs.read.db.BookDocument
import com.cqrs.read.service.BookReadServiceComponent

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
      case event: AddNewBook =>
        bookReadService.insert(new BookDocument(event)) pipeTo sender; ()
    }
  }

  object EventHandler {

    val name: String = "cqrs-event-handler"

    def apply: Props = Props(new EventHandler)
  }
}
