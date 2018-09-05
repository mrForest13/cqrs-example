package com.cqrs.example.service

import akka.actor.{ActorRef, ActorSystem}
import akka.stream.ActorMaterializer
import akka.testkit.{ImplicitSender, TestKit}
import com.cqrs.example._
import com.cqrs.example.db.Id
import com.cqrs.example.db.`type`.Language
import com.cqrs.example.db.model.{Author, Book, Category}
import com.cqrs.example.es.ElasticsearchContext
import com.cqrs.example.handler.EventHandlerComponent
import com.cqrs.example.service.read.{BookReadService, BookReadServiceComponent}
import com.cqrs.example.service.write._
import com.sksamuel.elastic4s.embedded.LocalNode
import com.sksamuel.elastic4s.http.ElasticClient
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach}

import scala.concurrent.ExecutionContextExecutor

class BookWriteServiceTest
    extends TestKit(ActorSystem("cqrs-system-test"))
    with ImplicitSender
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

  val localNode = LocalNode("mycluster", "/tmp/datapath")

  val esClient: ElasticClient = localNode.client(shutdownNodeOnClose = true)

  val bookReadService: BookReadService = new BookReadServiceImpl

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
    esClient.close()
  }

  "Book write service" should "return same element after insert new element" in {

    val pl = Language.fromCode("PL").get

    val author   = Author(None, "Jan", "Nowak")
    val category = Category(None, "Horror")
    val book     = Book(None, "Example", Id(1), "publisher", pl, Id(1), "description")

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
}
