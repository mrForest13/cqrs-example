package com.cqrs.read.service

import com.cqrs.common.event.EventSuccess
import com.cqrs.read.db.BookDocument
import com.cqrs.read.http.model.BookSearchParams
import com.sksamuel.elastic4s.http.ElasticDsl._
import com.sksamuel.elastic4s.sprayjson._
import com.typesafe.scalalogging.LazyLogging
import com.cqrs.read.utils.EsUtils._
import com.sksamuel.elastic4s.http.ElasticClient

import scala.concurrent.{ExecutionContext, Future}

trait BookService extends LazyLogging {
  def insert(id: String, book: BookDocument): Future[EventSuccess]
  def find(searchParams: BookSearchParams): Future[IndexedSeq[BookDocument]]
}

class BookServiceImpl(esClient: ElasticClient)(implicit ex: ExecutionContext) extends BookService with LazyLogging {

  def insert(id: String, book: BookDocument): Future[EventSuccess] = {

    val req = indexInto(BookDocument.indexName, book.category).withId(id).doc(book)

    logger.debug(s"Insert request: ${req.show}")

    esClient.execute(req).getResponse.map(res => EventSuccess(res.result))
  }

  def find(searchParams: BookSearchParams): Future[IndexedSeq[BookDocument]] = {

    val req = search(BookDocument.indexName).bool {
      boolQuery().must(
        Seq(
          optionRegexQuery("title", searchParams.title, (v: String) => v + ".*"),
          optionRegexQuery("author", searchParams.author, (v: String) => v + ".*"),
          optionMatchQuery("publisher", searchParams.publisher),
          optionMatchQuery("category", searchParams.category)
        ).flatten
      )
    }

    logger.debug(s"Search request: ${req.show}")

    esClient.execute(req).getResponse.map(_.to[BookDocument])
  }
}
