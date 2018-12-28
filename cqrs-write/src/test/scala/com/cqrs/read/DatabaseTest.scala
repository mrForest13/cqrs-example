package com.cqrs.read

import com.cqrs.write.db.{BaseDAO, BaseEntity}
import com.cqrs.write.{Core, DatabaseLayer}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Millis, Seconds, Span}
import org.scalatest.{FlatSpecLike, Matchers}

import scala.concurrent.Await
import scala.concurrent.duration._

trait DatabaseTest
    extends FlatSpecLike
    with ScalaFutures
    with Matchers
    with DatabaseLayer {

  this: Core =>

  implicit override val patienceConfig: PatienceConfig =
    PatienceConfig(timeout = Span(10, Seconds), interval = Span(50, Millis))

  implicit class TableInitiator[E <: BaseEntity[E]](dao: BaseDAO[E]) {

    private val timeout = 10.seconds

    def initScheme(): Unit = {
      Await.result(db.run(dao.initTable()), timeout)
    }

    def dropScheme(): Unit = {
      Await.result(db.run(dao.dropTable()), timeout)
    }
  }
}
