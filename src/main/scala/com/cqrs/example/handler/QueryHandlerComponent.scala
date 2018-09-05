package com.cqrs.example.handler

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.event.LoggingReceive
import com.cqrs.example.handler.model.GetBooks
import akka.pattern.pipe
import com.cqrs.example.service.read.BookReadServiceComponent

trait QueryHandlerComponent {

  this: BookReadServiceComponent =>

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
        bookReadService.find(searchParams) pipeTo sender; ()
    }
  }

  object QueryHandler {

    val name: String = "cqrs-query-handler"

    def apply: Props = Props(new QueryHandler)
  }
}
