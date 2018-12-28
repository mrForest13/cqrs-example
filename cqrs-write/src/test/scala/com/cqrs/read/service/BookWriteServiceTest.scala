package com.cqrs.read.service

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.stream.ActorMaterializer
import akka.testkit.TestKit
import com.cqrs.common.error.NotFoundException
import com.cqrs.common.event.{EventSuccess, BookAddedEvent}
import com.cqrs.read.{DatabaseTest, ExampleObject}
import com.cqrs.write.Core
import com.cqrs.write.db.`type`.Id
import com.cqrs.write.db.model.{Author, Book, Category}
import com.cqrs.write.handler.BookEventHandler
import com.cqrs.write.service._
import org.scalamock.scalatest.MockFactory
import com.softwaremill.tagging._
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach}

import scala.concurrent.ExecutionContext

class BookWriteServiceTest
    extends TestKit(ActorSystem("cqrs-system-test"))
    with MockFactory
    with Core
    with DatabaseTest
    with BeforeAndAfterEach
    with BeforeAndAfterAll {

  implicit lazy val executionContext: ExecutionContext = system.dispatcher
  implicit lazy val materializer: ActorMaterializer    = ActorMaterializer()

  val eventHandler: ActorRef @@ BookEventHandler = system.actorOf(Props(new Actor {
    override def receive: Receive = {
      case _: BookAddedEvent => sender ! EventSuccess("200")
    }
  })).taggedWith[BookEventHandler]

  val authorService: AuthorService     = new AuthorServiceImpl(authorDao)
  val categoryService: CategoryService = new CategoryServiceImpl(categoryDao)
  val bookService: BookService         = new BookServiceImpl(eventHandler, authorDao, categoryDao, bookDao)

  override def beforeEach(): Unit = {
    authorDao.initScheme()
    categoryDao.initScheme()
    bookDao.initScheme()
  }

  override def afterEach(): Unit = {
    bookDao.dropScheme()
    categoryDao.dropScheme()
    authorDao.dropScheme()
  }

  override def afterAll(): Unit = {
    system.stop(eventHandler)
    TestKit.shutdownActorSystem(system)
    db.close()
  }

  "Book write service" should "return same element after insert new element" in {

    val author   = new Author(ExampleObject.authorContent)
    val category = new Category(ExampleObject.categoryContent)
    val book     = new Book(Id(1), ExampleObject.bookContent)

    val action = for {
      _    <- authorService.add(author)
      _    <- categoryService.add(category)
      id   <- bookService.add(book)
      find <- db.run(bookDao.findById(id))
    } yield find

    whenReady(action) { result =>
      result.get shouldBe book.copy(id = Some(Id(1)))
    }
  }

  it should "throw exception after insert if author does not exist" in {

    val book = new Book(Id(1), ExampleObject.bookContent)

    val action = for {
      result <- bookService.add(book)
    } yield result

    whenReady(action.failed) { result =>
      result shouldBe a[NotFoundException]
    }
  }

  it should "throw exception after insert if category does not exist" in {

    val author = new Author(ExampleObject.authorContent)
    val book   = new Book(Id(1), ExampleObject.bookContent)

    val action = for {
      _      <- authorService.add(author)
      result <- bookService.add(book)
    } yield result

    whenReady(action.failed) { result =>
      result shouldBe a[NotFoundException]
    }
  }
}
