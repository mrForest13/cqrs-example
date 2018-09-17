package com.cqrs.write.handler

import akka.actor.{Actor, ActorLogging, Props}
import akka.event.LoggingReceive
import com.cqrs.write.db.model.{Author, Book, Category}
import com.cqrs.write.handler.model.{AddAuthorCommand, AddBookCommand, AddCategoryCommand}
import com.cqrs.write.db.Id
import akka.pattern.pipe
import com.cqrs.write.WriteServiceLayer
import com.cqrs.write.http.model.{AuthorContent, BookContent, CategoryContent}

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
      case AddAuthorCommand(content)         => addAuthor(content)
      case AddCategoryCommand(content)       => addCategory(content)
      case AddBookCommand(authorId, content) => addBook(authorId, content)
    }

    private def addAuthor(content: AuthorContent): Unit = {
      authorService.add(new Author(content)) pipeTo sender; ()
    }

    private def addCategory(content: CategoryContent): Unit = {
      categoryService.add(new Category(content)) pipeTo sender; ()
    }

    private def addBook(authorId: Id[Author], content: BookContent): Unit = {
      bookService.add(new Book(authorId, content)) pipeTo sender; ()
    }
  }

  object CommandHandler {

    val name: String = "cqrs-command-handler"

    def apply: Props = Props(new CommandHandler)
  }
}
