package com.cqrs.write.service

import akka.actor.ActorRef
import akka.pattern.ask
import akka.util.Timeout
import com.cqrs.common.error.NotFoundException
import com.cqrs.common.event.{BookAddedEvent, EventSuccess}
import com.cqrs.write.db.`type`.Id
import com.cqrs.write.db.dao.{AuthorDao, BookDao, CategoryDao}
import com.cqrs.write.db.model.Book
import com.cqrs.write.handler.BookEventHandler
import com.cqrs.write.utils.EventFactory
import com.softwaremill.tagging.@@
import slick.jdbc.JdbcBackend

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration._

trait BookService {
  def add(book: Book): Future[Id[Book]]
}

class BookServiceImpl(
  bookEventHandler: ActorRef @@ BookEventHandler,
  authorDao: AuthorDao,
  categoryDao: CategoryDao,
  bookDao: BookDao)(implicit db: JdbcBackend#Database, ex: ExecutionContext)
    extends BookService {

  import bookDao.profile.api._

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
        publishBookAddedEvent(EventFactory.bookAdded(book.withId(id), author, category))
      }
    } yield id

    db.run(action.transactionally)
  }

  private def publishBookAddedEvent(bookAddedEvent: BookAddedEvent): Future[EventSuccess] = {
    (bookEventHandler ? bookAddedEvent).mapTo[EventSuccess]
  }
}
