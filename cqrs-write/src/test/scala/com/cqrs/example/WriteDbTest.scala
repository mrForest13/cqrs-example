package com.cqrs.example

import com.cqrs.write.{Core, WriteDatabaseLayer}
import com.cqrs.write.db.Entity
import com.cqrs.write.db.dao.BaseDAO
import org.scalatest.{FlatSpecLike, Matchers}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Millis, Seconds, Span}

import scala.concurrent.duration._
import scala.concurrent.Await

trait WriteDbTest
    extends FlatSpecLike
    with ScalaFutures
    with Matchers
    with Core
    with WriteDatabaseLayer {

  implicit override val patienceConfig: PatienceConfig =
    PatienceConfig(timeout = Span(10, Seconds), interval = Span(50, Millis))

  implicit class TableInitiator[E <: Entity[E]](dao: BaseDAO[E]) {

    private val timeout = 10.seconds

    def initScheme(): Unit = {
      Await.result(db.run(dao.initTable()), timeout)
    }

    def dropScheme(): Unit = {
      Await.result(db.run(dao.dropTable()), timeout)
    }
  }
}
