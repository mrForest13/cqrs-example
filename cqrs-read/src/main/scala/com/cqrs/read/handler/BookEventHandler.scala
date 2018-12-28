package com.cqrs.read.handler

import akka.actor.{Actor, ActorLogging}
import akka.event.LoggingReceive
import com.cqrs.common.event.BookAddedEvent
import com.cqrs.read.db.BookDocument
import com.cqrs.read.service.BookService
import akka.pattern.pipe

class BookEventHandler(bookService: BookService) extends Actor with ActorLogging {

  import context.dispatcher

  override def preStart(): Unit = {
    super.preStart()
    log.info(s"Starting ${BookEventHandler.routeName} actor")
  }

  override def postStop(): Unit = {
    super.postStop()
    log.info(s"Stopping ${BookEventHandler.routeName} actor")
  }

  def receive: Receive = LoggingReceive {
    case event: BookAddedEvent => addNewBook(event)
  }

  private def addNewBook(event: BookAddedEvent): Unit = {
    bookService.insert(event.id, BookDocument(event)) pipeTo sender; ()
  }
}

object BookEventHandler {

  val routeName: String = "cqrs-book-event-handler"
}
