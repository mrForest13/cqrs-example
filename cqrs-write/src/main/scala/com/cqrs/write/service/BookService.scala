package com.cqrs.write.service

import akka.pattern.ask
import akka.util.Timeout
import com.cqrs.common.error.NotFoundException
import com.cqrs.common.event.EventSuccess
import com.cqrs.write.db.Id
import com.cqrs.write.db.model.Book
import com.cqrs.write.handler.EventHandlerComponent
import com.cqrs.write.utils.EventFactory._
import com.cqrs.write.{Core, DatabaseLayer}

import scala.concurrent.Future
import scala.concurrent.duration._

trait BookService {
  def add(book: Book): Future[Id[Book]]
}

trait BookServiceComponent {

  this: DatabaseLayer with EventHandlerComponent with Core =>

  val bookService: BookService

  class BookServiceImpl extends BookService {

    import profile.api._

    implicit val timeout: Timeout = Timeout(5.second)

    def add(book: Book): Future[Id[Book]] = {

      val action = for {
        author <- authorDao.findById(book.authorId).map {
          _.getOrElse(throw NotFoundException(s"Author does not exist"))
        }
        category <- categoryDao.findById(book.categoryId).map {
          _.getOrElse(throw NotFoundException(s"Category does not exist"))
        }
        id <- bookDao.insert(book)
        _ <- DBIO.from {
          (eventHandler ? newBookAdded(book.withId(id), author, category)).mapTo[EventSuccess]
        }
      } yield id

      db.run(action.transactionally)
    }
  }
}
