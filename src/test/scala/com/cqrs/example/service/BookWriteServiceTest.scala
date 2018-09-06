package com.cqrs.example.service

import akka.actor.{ActorRef, ActorSystem}
import akka.stream.ActorMaterializer
import akka.testkit.{ImplicitSender, TestKit}
import com.cqrs.example._
import com.cqrs.example.db.Id
import com.cqrs.example.db.`type`.Language
import com.cqrs.example.db.model.{Author, Book, Category}
import com.cqrs.example.es.{BookDocument, ElasticsearchContext}
import com.cqrs.example.handler.EventHandlerComponent
import com.cqrs.example.service.read.{BookReadService, BookReadServiceComponent}
import com.cqrs.example.service.write._
import com.cqrs.example.utils.NotFoundException
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
    with AuthorWriteServiceComponent
    with CategoryWriteServiceComponent
    with BookWriteServiceComponent
    with BeforeAndAfterEach
    with BeforeAndAfterAll {

  implicit val executionContext: ExecutionContextExecutor = system.dispatcher
  implicit lazy val materializer: ActorMaterializer       = ActorMaterializer()

  val esClient: ElasticClient = mock[ElasticClient]

  val bookReadService: BookReadService = mock[BookReadService]

  val eventHandler: ActorRef = system.actorOf(EventHandler.apply, EventHandler.name)

  val authorWriteService: AuthorWriteService     = new AuthorWriteServiceImpl
  val categoryWriteService: CategoryWriteService = new CategoryWriteServiceImpl
  val bookWriteService: write.BookWriteService   = new BookWriteServiceImpl

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

  val pl: Language = Language.fromCode("PL").get

  "Book write service" should "return same element after insert new element" in {

    val author   = Author(None, "Jan", "Nowak")
    val category = Category(None, "Horror")
    val book     = Book(None, "Example", Id(1), "publisher", pl, Id(1), "description")

    val bookDoc = new BookDocument(book, author, category)

    bookReadService.insert _ expects bookDoc returning Future(EsHelper.emptyIndexRes)

    val action = for {
      _    <- authorWriteService.add(author)
      _    <- categoryWriteService.add(category)
      _    <- bookWriteService.add(book)
      find <- db.run(bookDao.findById(Id(1)))
    } yield find

    whenReady(action) { result =>
      result.get shouldBe book.copy(id = Some(Id(1)))
    }
  }

  it should "throw exception after insert if author does not exist" in {

    val book = Book(None, "Example", Id(1), "publisher", pl, Id(1), "description")

    val action = for {
      result <- bookWriteService.add(book)
    } yield result

    whenReady(action.failed) { result =>
      result shouldBe a[NotFoundException]
    }
  }

  it should "throw exception after insert if category does not exist" in {

    val author = Author(None, "Jan", "Nowak")
    val book   = Book(None, "Example", Id(1), "publisher", pl, Id(1), "description")

    val action = for {
      _      <- authorWriteService.add(author)
      result <- bookWriteService.add(book)
    } yield result

    whenReady(action.failed) { result =>
      result shouldBe a[NotFoundException]
    }
  }
}
