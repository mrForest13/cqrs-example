package com.cqrs.read.handler

import akka.actor.{Actor, ActorLogging}
import akka.event.LoggingReceive
import com.cqrs.read.handler.model.GetBooks
import akka.pattern.pipe
import com.cqrs.read.service.BookService

class BookQueryHandler(bookService: BookService) extends Actor with ActorLogging {

  import context.dispatcher

  override def preStart(): Unit = {
    super.preStart()
    log.info(s"Starting ${BookQueryHandler.name} actor")
  }

  override def postStop(): Unit = {
    super.postStop()
    log.info(s"Stopping ${BookQueryHandler.name} actor")
  }

  def receive: Receive = LoggingReceive {
    case GetBooks(searchParams) =>
      bookService.find(searchParams) pipeTo sender; ()
  }
}

object BookQueryHandler {

  val name: String = "cqrs-book-query-handler"
}
