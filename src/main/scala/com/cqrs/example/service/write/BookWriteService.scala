package com.cqrs.example.service.write

import com.cqrs.example.db.model.Book
import com.cqrs.example.utils.NotFoundException
import com.cqrs.example.{Core, WriteDatabaseLayer}
import akka.pattern.ask
import akka.util.Timeout
import com.cqrs.example.es.BookDocument
import com.cqrs.example.handler.EventHandlerComponent
import com.cqrs.example.handler.model.InsertBookToReadDbEvent
import com.sksamuel.elastic4s.http.index.IndexResponse

import scala.concurrent.duration._
import scala.concurrent.Future

trait BookWriteService {
  def add(book: Book): Future[Unit]
}

trait BookWriteServiceComponent {

  this: WriteDatabaseLayer with EventHandlerComponent with Core =>

  val bookWriteService: BookWriteService

  class BookWriteServiceImpl extends BookWriteService {

    import profile.api._

    implicit val timeout: Timeout = Timeout(5.second)

    def add(book: Book): Future[Unit] = {

      val action = for {
        author <- authorDao.findById(book.authorId).map {
          _.getOrElse(throw NotFoundException(s"Author does not exist"))
        }
        category <- categoryDao.findById(book.categoryId).map {
          _.getOrElse(throw NotFoundException(s"Category does not exist"))
        }
        _ <- bookDao.insert(book)
      } yield new BookDocument(book, author, category)

      for {
        bookDoc <- db.run(action.transactionally)
        _       <- (eventHandler ? InsertBookToReadDbEvent(bookDoc)).mapTo[IndexResponse]
      } yield ()
    }
  }
}
