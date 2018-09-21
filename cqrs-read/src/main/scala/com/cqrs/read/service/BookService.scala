package com.cqrs.read.service

import com.cqrs.read.Core
import com.cqrs.read.db.{BookDocument, ElasticsearchContext}
import com.cqrs.read.http.model.BookSearchParams
import com.sksamuel.elastic4s.http.ElasticDsl._
import com.sksamuel.elastic4s.sprayjson._
import com.typesafe.scalalogging.LazyLogging
import com.cqrs.read.utils.EsUtils._

import scala.concurrent.Future

trait BookService {
  def find(searchParams: BookSearchParams): Future[IndexedSeq[BookDocument]]
}

trait BookServiceComponent {

  this: ElasticsearchContext with Core =>

  val bookService: BookService

  class BookServiceImpl extends BookService with LazyLogging {

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
}
