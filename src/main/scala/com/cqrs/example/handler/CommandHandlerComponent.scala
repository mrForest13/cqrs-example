package com.cqrs.example.handler

import akka.actor.{Actor, ActorLogging, Props}
import akka.event.LoggingReceive
import com.cqrs.example.WriteServiceLayer
import com.cqrs.example.db.model.{Author, Book, Category}
import com.cqrs.example.handler.model.{AddAuthor, AddBook, AddCategory}
import com.cqrs.example.db.Id
import akka.pattern.pipe
import com.cqrs.example.http.model.{AuthorContent, BookContent, CategoryContent}

trait CommandHandlerComponent {

  this: WriteServiceLayer =>

  final class CommandHandler extends Actor with ActorLogging {

    import context.dispatcher

    override def preStart(): Unit = {
      super.preStart()
      log.info(s"Starting ${CommandHandler.name} actor")
    }

    override def postStop(): Unit = {
      super.postStop()
      log.info(s"Stopping ${CommandHandler.name} actor")
    }

    def receive: Receive = LoggingReceive {
      case AddAuthor(content)         => addAuthor(content)
      case AddCategory(content)       => addCategory(content)
      case AddBook(authorId, content) => addBook(authorId, content)
    }

    private def addAuthor(content: AuthorContent): Unit = {
      authorWriteService.add(new Author(content)) pipeTo sender; ()
    }

    private def addCategory(content: CategoryContent): Unit = {
      categoryWriteService.add(new Category(content)) pipeTo sender; ()
    }

    private def addBook(authorId: Id[Author], content: BookContent): Unit = {
      bookWriteService.add(new Book(authorId, content)) pipeTo sender; ()
    }
  }

  object CommandHandler {

    val name: String = "cqrs-command-handler"

    def apply: Props = Props(new CommandHandler)
  }
}
