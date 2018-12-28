package com.cqrs.write.http

import akka.actor.ActorRef
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import com.cqrs.common.log.ResponseLogger._
import com.cqrs.common.validation.ValidationDirective._
import com.cqrs.write.db.`type`.Id
import com.cqrs.write.handler.BookCommandHandler
import com.cqrs.write.handler.model.AddBookCommand
import com.cqrs.write.http.doc.BookApiDoc
import com.cqrs.write.http.model.BookContent
import com.softwaremill.tagging.@@

final class BookApi(handler: ActorRef @@ BookCommandHandler) extends BookApiDoc with Api {

  override def routes: Route = log(addBook)

  def addBook: Route = {
    (post & path("cqrs" / "author" / LongNumber / "book")) { authorId =>
      entity(as[BookContent]) { content =>
        validateReq(content).apply {
          onSuccess(handler ? AddBookCommand(Id(authorId), content)) { _ =>
            complete(StatusCodes.Created)
          }
        }
      }
    }
  }
}
