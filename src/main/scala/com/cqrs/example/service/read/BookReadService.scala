package com.cqrs.example.service.read

import com.cqrs.example.{Core, ReadDatabaseLayer}
import com.cqrs.example.es.BookDocument
import com.cqrs.example.http.model.BookSearchParams
import com.sksamuel.elastic4s.http.ElasticDsl._
import com.sksamuel.elastic4s.http.Response
import com.sksamuel.elastic4s.http.index.IndexResponse
import com.sksamuel.elastic4s.sprayjson._
import com.typesafe.scalalogging.LazyLogging
import com.cqrs.example.utils.EsUtils._

import scala.concurrent.Future

trait BookReadService {
  def insert(book: BookDocument): Future[Response[IndexResponse]]
  def find(searchParams: BookSearchParams): Future[IndexedSeq[BookDocument]]
}

trait BookReadServiceComponent {

  this: ReadDatabaseLayer with Core =>

  val bookReadService: BookReadService

  class BookReadServiceImpl extends BookReadService with LazyLogging {

    def insert(book: BookDocument): Future[Response[IndexResponse]] = {

      val req = indexInto(BookDocument.indexName, BookDocument.mappingName).doc(book)

      logger.debug(s"Insert request: ${req.show}")

      logResponse(logger, "Insert book document") {
        esClient.execute(req)
      }
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

      logResponse(logger, s"Search book document by $searchParams") {
        esClient.execute(req)
      }.map(_.result.to[BookDocument])
    }
  }
}