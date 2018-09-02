package com.cqrs.example.service.read

import com.cqrs.example.ReadDatabaseLayer
import com.cqrs.example.es.BookDocument
import com.sksamuel.elastic4s.http.ElasticDsl._
import com.sksamuel.elastic4s.http.Response
import com.sksamuel.elastic4s.http.index.IndexResponse
import com.sksamuel.elastic4s.sprayjson._

import scala.concurrent.Future

trait BookReadService {
  def insert(book: BookDocument): Future[Response[IndexResponse]]
}

trait BookReadServiceComponent {

  this: ReadDatabaseLayer =>

  val bookReadService: BookReadService

  class BookReadServiceImpl extends BookReadService {

    def insert(book: BookDocument): Future[Response[IndexResponse]] = {
      esClient.execute {
        indexInto(BookDocument.indexName, BookDocument.mappingName).doc(book)
      }
    }
  }
}
