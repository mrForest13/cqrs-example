package com.cqrs.write.service

import akka.pattern.ask
import akka.util.Timeout
import com.cqrs.common.error.NotFoundException
import com.cqrs.write.db.model.Book
import com.cqrs.write.handler.EventHandlerComponent
import com.cqrs.write.{Core, WriteDatabaseLayer}
import com.sksamuel.elastic4s.http.index.IndexResponse

import scala.concurrent.Future
import scala.concurrent.duration._

trait BookService {
  def add(book: Book): Future[Unit]
}

trait BookServiceComponent {

  this: WriteDatabaseLayer with EventHandlerComponent with Core =>

  val bookService: BookService

  class BookServiceImpl extends BookService {

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
        _       <- (eventHandler ? InsertBookToReadDb(bookDoc)).mapTo[IndexResponse]
      } yield ()
    }
  }
}
