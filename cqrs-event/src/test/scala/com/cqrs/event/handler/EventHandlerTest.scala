package com.cqrs.event.handler

import akka.actor.{ActorRef, ActorSystem}
import akka.pattern.ask
import akka.stream.ActorMaterializer
import akka.testkit.{ImplicitSender, TestKit}
import akka.util.Timeout
import com.cqrs.common.event.{EventSuccess, NewBookAddedEvent}
import com.cqrs.event.Core
import com.cqrs.event.db.ElasticsearchContext
import com.cqrs.event.db.model.BookDocument
import com.cqrs.event.service.{BookService, BookServiceComponent}
import com.sksamuel.elastic4s.http.ElasticClient
import org.scalamock.scalatest.MockFactory
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{BeforeAndAfterAll, FlatSpecLike, Matchers}

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}
import scala.language.higherKinds

class EventHandlerTest
    extends TestKit(ActorSystem("cqrs-system-test"))
    with ImplicitSender
    with MockFactory
    with FlatSpecLike
    with ScalaFutures
    with Matchers
    with Core
    with ElasticsearchContext
    with BookServiceComponent
    with EventHandlerComponent
    with BeforeAndAfterAll {

  override def afterAll(): Unit = {
    system.stop(eventHandler)
    TestKit.shutdownActorSystem(system)
  }

  implicit val timeout: Timeout = Timeout(5.second)

  implicit lazy val executionContext: ExecutionContext = system.dispatcher
  implicit lazy val materializer: ActorMaterializer    = ActorMaterializer()

  val esClient: ElasticClient = mock[ElasticClient]

  val bookService: BookService = mock[BookService]

  val eventHandler: ActorRef = system.actorOf(EventHandler.apply, EventHandler.name)

  "Event Handler" should "respond with EventSuccess" in {

    val event =
      NewBookAddedEvent("1", "title", "author", "publisher", "language", "category", "description")

    val bookDoc = BookDocument(event)

    bookService.insert _ expects (event.id, bookDoc) returning Future(EventSuccess("200"))

    val eventResponse = (eventHandler ? event).mapTo[EventSuccess]

    whenReady(eventResponse) { result =>
      result shouldBe EventSuccess("200")
    }
  }
}
