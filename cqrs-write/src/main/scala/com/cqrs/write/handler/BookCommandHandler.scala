package com.cqrs.write.handler

import akka.actor.{Actor, ActorLogging}
import akka.event.LoggingReceive
import akka.pattern.pipe
import com.cqrs.write.db.`type`.Id
import com.cqrs.write.db.model.{Author, Book}
import com.cqrs.write.handler.model.AddBookCommand
import com.cqrs.write.http.model.BookContent
import com.cqrs.write.service.BookService

class BookCommandHandler(bookService: BookService) extends Actor with ActorLogging {

  import context.dispatcher

  override def preStart(): Unit = {
    super.preStart()
    log.info(s"Starting ${BookCommandHandler.name} actor")
  }

  override def postStop(): Unit = {
    super.postStop()
    log.info(s"Stopping ${BookCommandHandler.name} actor")
  }

  def receive: Receive = LoggingReceive {
    case AddBookCommand(authorId, content) => addBook(authorId, content)
  }

  private def addBook(authorId: Id[Author], content: BookContent): Unit = {
    bookService.add(new Book(authorId, content)) pipeTo sender; ()
  }
}

object BookCommandHandler {

  val name: String = "cqrs-book-command-handler"
}
