package com.cqrs.event.service

import com.cqrs.common.event.EventSuccess
import com.cqrs.event.Core
import com.cqrs.event.db.ElasticsearchContext
import com.cqrs.event.db.model.BookDocument
import com.cqrs.event.utils.EsUtils._
import com.sksamuel.elastic4s.http.ElasticDsl._
import com.sksamuel.elastic4s.sprayjson._
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.Future

trait BookService {
  def insert(id: String, book: BookDocument): Future[EventSuccess]
}

trait BookServiceComponent {

  this: ElasticsearchContext with Core =>

  val bookService: BookService

  class BookServiceImpl extends BookService with LazyLogging {

    def insert(id: String, book: BookDocument): Future[EventSuccess] = {

      val req = indexInto(BookDocument.indexName, book.category).withId(id).doc(book)

      logger.debug(s"Insert request: ${req.show}")

      esClient.execute(req).getResponse.map(res => EventSuccess(res.result))
    }
  }
}
