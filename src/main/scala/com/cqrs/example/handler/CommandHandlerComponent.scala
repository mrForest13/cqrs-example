package com.cqrs.example.handler

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.event.LoggingReceive
import com.cqrs.example.ServiceLayer
import com.cqrs.example.db.model.{Author, Book, Category}
import com.cqrs.example.handler.model.{AddAuthor, AddBook, AddCategory}
import akka.pattern.pipe
import com.cqrs.example.db.Id
import com.cqrs.example.http.model.{AuthorContent, BookContent, CategoryContent}

trait CommandHandlerComponent {

  this: ServiceLayer =>

  val commandHandler: ActorRef

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
      val newAuthor = Author(None, content.firstName, content.lastName)
      authorService.add(newAuthor) pipeTo sender; ()
    }

    private def addCategory(content: CategoryContent): Unit = {
      val newCategory = Category(None, content.name)
      categoryService.add(newCategory) pipeTo sender; ()
    }

    private def addBook(authorId: Id[Author], content: BookContent): Unit = {
      val newBook = Book(None,
                         content.title,
                         authorId,
                         content.publisher,
                         content.language,
                         content.categoryId,
                         content.description)
      bookService.add(newBook) pipeTo sender; ()
    }
  }

  object CommandHandler {

    val name: String = "cqrs-command-handler"

    def apply: Props = Props(new CommandHandler)
  }
}
