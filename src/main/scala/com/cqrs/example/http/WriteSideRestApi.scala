package com.cqrs.example.http
import akka.actor.ActorRef
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import com.cqrs.example.handler.model.{AddAuthorCommand, AddBookCommand, AddCategoryCommand}
import com.cqrs.example.utils.ResponseLogger._
import akka.pattern.ask
import com.cqrs.example.db.Id
import com.cqrs.example.http.model.{AuthorContent, BookContent, CategoryContent}

final class WriteSideRestApi(handler: ActorRef) extends RestApi {

  override def routes: Route = log(addAuthor ~ addCategory ~ addBook)

  def addAuthor: Route = {
    (post & path("cqrs" / "author")) {
      entity(as[AuthorContent]) { content =>
        onSuccess(handler ? AddAuthorCommand(content)) { _ =>
          complete(StatusCodes.Created)
        }
      }
    }
  }

  def addCategory: Route = {
    (post & path("cqrs" / "category")) {
      entity(as[CategoryContent]) { content =>
        onSuccess(handler ? AddCategoryCommand(content)) { _ =>
          complete(StatusCodes.Created)
        }
      }
    }
  }

  def addBook: Route = {
    (post & path("cqrs" / "author" / LongNumber / "book")) { authorId =>
      entity(as[BookContent]) { content =>
        onSuccess(handler ? AddBookCommand(Id(authorId), content)) { _ =>
          complete(StatusCodes.Created)
        }
      }
    }
  }
}
