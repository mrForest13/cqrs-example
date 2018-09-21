package com.cqrs.read.handler

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.event.LoggingReceive
import com.cqrs.read.handler.model.GetBooks
import akka.pattern.pipe
import com.cqrs.read.service.BookServiceComponent

trait QueryHandlerComponent {

  this: BookServiceComponent =>

  val queryHandler: ActorRef

  class QueryHandler extends Actor with ActorLogging {

    import context.dispatcher

    override def preStart(): Unit = {
      super.preStart()
      log.info(s"Starting ${QueryHandler.name} actor")
    }

    override def postStop(): Unit = {
      super.postStop()
      log.info(s"Stopping ${QueryHandler.name} actor")
    }

    def receive: Receive = LoggingReceive {
      case GetBooks(searchParams) =>
        bookService.find(searchParams) pipeTo sender; ()
    }
  }

  object QueryHandler {

    val name: String = "cqrs-query-handler"

    def apply: Props = Props(new QueryHandler)
  }
}
