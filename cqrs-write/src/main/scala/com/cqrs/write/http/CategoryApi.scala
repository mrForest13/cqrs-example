package com.cqrs.write.http

import akka.actor.ActorRef
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import com.cqrs.common.log.ResponseLogger.log
import com.cqrs.common.validation.ValidationDirective.validateReq
import com.cqrs.write.handler.CategoryCommandHandler
import com.cqrs.write.handler.model.AddCategoryCommand
import com.cqrs.write.http.doc.CategoryApiDoc
import com.cqrs.write.http.model.CategoryContent
import com.softwaremill.tagging.@@

final class CategoryApi(handler: ActorRef @@ CategoryCommandHandler) extends CategoryApiDoc with Api {

  override def routes: Route = log(addCategory)

  def addCategory: Route = {
    (post & path("cqrs" / "category")) {
      entity(as[CategoryContent]) { content =>
        validateReq(content).apply {
          onSuccess(handler ? AddCategoryCommand(content)) { _ =>
            complete(StatusCodes.Created)
          }
        }
      }
    }
  }
}
