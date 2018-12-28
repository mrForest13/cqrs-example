package com.cqrs.write.handler

import akka.actor.{Actor, ActorLogging}
import akka.event.LoggingReceive
import akka.pattern.pipe
import com.cqrs.write.db.model.Category
import com.cqrs.write.handler.model.AddCategoryCommand
import com.cqrs.write.http.model.CategoryContent
import com.cqrs.write.service.CategoryService

class CategoryCommandHandler(categoryService: CategoryService) extends Actor with ActorLogging {

  import context.dispatcher

  override def preStart(): Unit = {
    super.preStart()
    log.info(s"Starting ${CategoryCommandHandler.name} actor")
  }

  override def postStop(): Unit = {
    super.postStop()
    log.info(s"Stopping ${CategoryCommandHandler.name} actor")
  }

  def receive: Receive = LoggingReceive {
    case AddCategoryCommand(content) => addCategory(content)
  }

  private def addCategory(content: CategoryContent): Unit = {
    categoryService.add(new Category(content)) pipeTo sender; ()
  }
}

object CategoryCommandHandler {

  val name: String = "cqrs-category-command-handler"
}
