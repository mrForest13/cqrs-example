package com.cqrs.write.handler

import akka.actor.{Actor, ActorLogging}
import akka.event.LoggingReceive
import akka.pattern.pipe
import com.cqrs.write.db.model.Author
import com.cqrs.write.handler.model.AddAuthorCommand
import com.cqrs.write.http.model.AuthorContent
import com.cqrs.write.service.AuthorService

class AuthorCommandHandler(authorService: AuthorService) extends Actor with ActorLogging {

  import context.dispatcher

  override def preStart(): Unit = {
    super.preStart()
    log.info(s"Starting ${AuthorCommandHandler.name} actor")
  }

  override def postStop(): Unit = {
    super.postStop()
    log.info(s"Stopping ${AuthorCommandHandler.name} actor")
  }

  def receive: Receive = LoggingReceive {
    case AddAuthorCommand(content) => addAuthor(content)
  }

  private def addAuthor(content: AuthorContent): Unit = {
    authorService.add(new Author(content)) pipeTo sender; ()
  }
}

object AuthorCommandHandler {

  val name: String = "cqrs-author-command-handler"
}
