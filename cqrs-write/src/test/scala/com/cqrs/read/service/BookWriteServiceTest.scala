package com.cqrs.write.service

import akka.actor.{ActorRef, ActorSystem}
import akka.stream.ActorMaterializer
import akka.testkit.{ImplicitSender, TestKit}
import com.cqrs.example._
import com.cqrs.read.service.{BookReadService, BookReadServiceComponent}
import com.cqrs.write.db.Id
import com.cqrs.write.db.model.{Author, Book, Category}
import com.cqrs.write.es.{BookDocument, ElasticsearchContext}
import com.cqrs.write.handler.EventHandlerComponent
import com.cqrs.write.service.read.BookReadServiceComponent
import com.cqrs.write.service.write._
import com.cqrs.write.utils.NotFoundException
import com.sksamuel.elastic4s.http.ElasticClient
import org.scalamock.scalatest.MockFactory
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach}

import scala.language.higherKinds
import scala.concurrent.{ExecutionContextExecutor, Future}

class BookWriteServiceTest
    extends TestKit(ActorSystem("cqrs-system-test"))
    with ImplicitSender
    with MockFactory
    with WriteDbTest
    with ElasticsearchContext
    with BookReadServiceComponent
    with EventHandlerComponent
    with AuthorServiceComponent
    with CategoryServiceComponent
    with BookServiceComponent
    with BeforeAndAfterEach
    with BeforeAndAfterAll {

  implicit val executionContext: ExecutionContextExecutor = system.dispatcher
  implicit lazy val materializer: ActorMaterializer       = ActorMaterializer()

  val esClient: ElasticClient = mock[ElasticClient]

  val bookReadService: BookReadService = mock[BookReadService]

  val eventHandler: ActorRef = system.actorOf(EventHandler.apply, EventHandler.name)

  val authorService: AuthorService     = new AuthorServiceImpl
  val categoryService: CategoryService = new CategoryServiceImpl
  val bookService: BookService   = new BookServiceImpl

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
    TestKit.shutdownActorSystem(system)
    db.close()
  }

  "Book write service" should "return same element after insert new element" in {

    val author   = new Author(ExampleObject.authorContent)
    val category = new Category(ExampleObject.categoryContent)
    val book     = new Book(Id(1), ExampleObject.bookContent)

    val bookDoc = new BookDocument(book.copy(id = Some(Id(1))), author, category)

    bookReadService.insert _ expects bookDoc returning Future(EsHelper.emptyIndexRes)

    val action = for {
      _    <- authorService.add(author)
      _    <- categoryService.add(category)
      _    <- bookService.add(book)
      find <- db.run(bookDao.findById(Id(1)))
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
