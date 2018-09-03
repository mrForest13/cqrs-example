package com.cqrs.example.service.read

import com.cqrs.example.Core
import com.cqrs.example.es.{BookDocument, ElasticsearchContext}
import com.cqrs.example.http.model.BookSearchParams
import com.sksamuel.elastic4s.http.ElasticDsl._
import com.sksamuel.elastic4s.http.index.IndexResponse
import com.sksamuel.elastic4s.sprayjson._
import com.typesafe.scalalogging.LazyLogging
import com.cqrs.example.utils.EsUtils._

import scala.concurrent.Future

trait BookReadService {
  def insert(book: BookDocument): Future[IndexResponse]
  def find(searchParams: BookSearchParams): Future[IndexedSeq[BookDocument]]
}

trait BookReadServiceComponent {

  this: ElasticsearchContext with Core =>

  val bookReadService: BookReadService

  class BookReadServiceImpl extends BookReadService with LazyLogging {

    def insert(book: BookDocument): Future[IndexResponse] = {

      val req = indexInto(BookDocument.indexName, BookDocument.mappingName).doc(book)

      logger.debug(s"Insert request: ${req.show}")

      esClient.execute(req).getResponse
    }

    def find(searchParams: BookSearchParams): Future[IndexedSeq[BookDocument]] = {

      val req = search(BookDocument.indexName).bool {
        boolQuery().must(
          Seq(
            optionMatchQuery("title", searchParams.title),
            optionMatchQuery("author", searchParams.author),
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
