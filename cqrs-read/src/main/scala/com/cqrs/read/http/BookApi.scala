package com.cqrs.read.http

import akka.actor.ActorRef
import akka.http.scaladsl.server.{Directive1, Route}
import com.cqrs.read.http.model.BookSearchParams
import com.cqrs.common.log.ResponseLogger._
import akka.pattern.ask
import com.cqrs.read.db.BookDocument
import com.cqrs.read.handler.model.GetBooks
import com.cqrs.read.http.doc.BookApiDoc
import spray.json.DefaultJsonProtocol._
import com.cqrs.common.validation.ValidationDirective._
import com.cqrs.read.handler.BookQueryHandler
import com.softwaremill.tagging.@@

final class BookApi(handler: ActorRef @@ BookQueryHandler) extends BookApiDoc with Api {

  override def routes: Route = log(getBooks)

  def getBooks: Route = {
    (get & path("cqrs" / "book")) {
      searchParams { params =>
        validateReq(params).apply {
          complete {
            (handler ? GetBooks(params)).mapTo[IndexedSeq[BookDocument]]
          }
        }
      }
    }
  }

  private def searchParams: Directive1[BookSearchParams] = {
    val params = (
      'title.as[String].?,
      'author.as[String].?,
      'publisher.as[String].?,
      'category.as[String].?
    )

    parameters(params).as(BookSearchParams.apply)
  }
}
