package com.cqrs.example

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.testkit.{ImplicitSender, TestKit}
import com.cqrs.example.db.HasId
import com.cqrs.example.db.dao.BaseDAO
import org.scalatest.{FlatSpecLike, Matchers}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Millis, Seconds, Span}

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContextExecutor}

class CqrsTest
    extends TestKit(ActorSystem("cqrs-system-test"))
    with ImplicitSender
    with Matchers
    with ScalaFutures
    with FlatSpecLike
    with Core
    with WriteDatabaseLayer
    with ReadDatabaseLayer
    with ReadServiceLayer
    with EventHandlerLayer
    with WriteServiceLayer {

  implicit val executionContext: ExecutionContextExecutor = system.dispatcher
  implicit lazy val materializer: ActorMaterializer       = ActorMaterializer()

  implicit override val patienceConfig: PatienceConfig =
    PatienceConfig(timeout = Span(10, Seconds), interval = Span(50, Millis))

  implicit class TableInitiator[E <: HasId[E]](dao: BaseDAO[E]) {

    private val timeout = 10.seconds

    def initScheme(): Unit = {
      Await.result(db.run(dao.initTable()), timeout)
    }

    def dropScheme(): Unit = {
      Await.result(db.run(dao.dropTable()), timeout)
    }
  }
}
