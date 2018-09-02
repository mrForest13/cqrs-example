package com.cqrs.example.service.write

import com.cqrs.example.db.model.Book
import com.cqrs.example.utils.NotFoundException
import com.cqrs.example.{Core, EventHandlerLayer, WriteDatabaseLayer}
import akka.pattern.ask
import akka.util.Timeout
import com.cqrs.example.es.BookDocument
import com.cqrs.example.handler.model.InsertBookToReadDb
import scala.concurrent.duration._

import scala.concurrent.Future

trait BookWriteService {
  def add(book: Book): Future[Unit]
}

trait BookWriteServiceComponent {

  this: WriteDatabaseLayer with EventHandlerLayer with Core =>

  val bookWriteService: BookWriteService

  class BookWriteServiceImpl extends BookWriteService {

    import profile.api._

    implicit val timeout: Timeout = Timeout(5.second)

    def add(book: Book): Future[Unit] = {

      val action = for {
        author <- authorDao.findById(book.authorId).map { elem =>
          if (elem.isDefined) throw NotFoundException(s"Author does not exists") else elem.get
        }
        category <- categoryDao.findById(book.categoryId).map { elem =>
          if (elem.isDefined) throw NotFoundException(s"Category does not exists") else elem.get
        }
        _ <- bookDao.insert(book)
      } yield new BookDocument(book, author, category)

      for {
        bookDoc <- db.run(action.transactionally)
        _       <- eventHandler ? InsertBookToReadDb(bookDoc)
      } yield ()
    }
  }
}
