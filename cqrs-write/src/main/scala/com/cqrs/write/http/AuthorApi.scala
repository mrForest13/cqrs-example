package com.cqrs.write.http

import akka.actor.ActorRef
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import com.cqrs.common.log.ResponseLogger.log
import com.cqrs.common.validation.ValidationDirective.validateReq
import com.cqrs.write.handler.AuthorCommandHandler
import com.cqrs.write.handler.model.AddAuthorCommand
import com.cqrs.write.http.doc.AuthorApiDoc
import com.cqrs.write.http.model.AuthorContent
import com.softwaremill.tagging.@@

final class AuthorApi(handler: ActorRef @@ AuthorCommandHandler) extends AuthorApiDoc with Api {

  override def routes: Route = log(addAuthor)

  def addAuthor: Route = {
    (post & path("cqrs" / "author")) {
      entity(as[AuthorContent]) { content =>
        validateReq(content).apply {
          onSuccess(handler ? AddAuthorCommand(content)) { _ =>
            complete(StatusCodes.Created)
          }
        }
      }
    }
  }
}
