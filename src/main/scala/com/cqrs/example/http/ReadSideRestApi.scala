package com.cqrs.example.http

import akka.actor.ActorRef
import akka.http.scaladsl.server.{Directive1, Route}
import com.cqrs.example.http.model.BookSearchParams
import com.cqrs.example.utils.ResponseLogger._
import akka.pattern.ask
import com.cqrs.example.es.BookDocument
import com.cqrs.example.handler.model.GetBooks
import com.cqrs.example.http.doc.ReadSideDoc
import spray.json.DefaultJsonProtocol._
import com.cqrs.example.utils.ValidationDirective._

final class ReadSideRestApi(handler: ActorRef) extends ReadSideDoc with RestApi {

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
