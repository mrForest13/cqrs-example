package com.cqrs.example.service

import com.cqrs.example.{Core, DataBaseLayer}
import com.cqrs.example.db.model.Book
import com.cqrs.example.utils.NotFoundException

import scala.concurrent.Future

trait BookService {
  def add(book: Book): Future[Unit]
}

trait BookServiceComponent {

  this: DataBaseLayer with Core =>

  val bookService: BookService

  class BookServiceImpl extends BookService {

    import profile.api._

    def add(book: Book): Future[Unit] = {

      val action = for {
        _ <- authorDao.findById(book.authorId).map { elem =>
          if (elem.isDefined) throw NotFoundException(s"Author does not exists")
        }
        _ <- categoryDao.findById(book.categoryId).map { elem =>
          if (elem.isDefined) throw NotFoundException(s"Category does not exists")
        }
        _ <- bookDao.insert(book)
      } yield ()

      db.run(action.transactionally)
    }
  }
}
