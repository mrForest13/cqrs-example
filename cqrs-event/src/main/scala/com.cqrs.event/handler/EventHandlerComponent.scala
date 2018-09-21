package com.cqrs.event.handler

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.event.LoggingReceive
import com.cqrs.common.event.NewBookAddedEvent
import com.cqrs.event.service.BookServiceComponent
import akka.pattern.pipe
import com.cqrs.event.db.model.BookDocument

trait EventHandlerComponent {

  this: BookServiceComponent =>

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
      case event: NewBookAddedEvent => addNewBook(event)
    }

    private def addNewBook(event: NewBookAddedEvent): Unit = {
      bookService.insert(event.id, BookDocument(event)) pipeTo sender; ()
    }
  }

  object EventHandler {

    val name: String = "cqrs-event-handler"

    def apply: Props = Props(new EventHandler)
  }
}
