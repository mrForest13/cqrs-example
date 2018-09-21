package com.cqrs.event

import akka.actor.{ActorRef, ActorSystem}
import akka.stream.ActorMaterializer
import com.cqrs.event.config.{AppConfig, Config}
import com.cqrs.event.db.ElasticsearchContext
import com.cqrs.event.handler.EventHandlerComponent
import com.cqrs.event.service.{BookService, BookServiceComponent}
import com.sksamuel.elastic4s.http.{ElasticClient, ElasticProperties}
import com.typesafe.scalalogging.StrictLogging

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext}

trait Core {
  implicit val system: ActorSystem
  implicit val materializer: ActorMaterializer
  implicit val executionContext: ExecutionContext

  val config: Config = AppConfig.config
}

trait BootedCore extends Core with StrictLogging {

  implicit val system: ActorSystem                = ActorSystem(config.app.actorSystem)
  implicit val materializer: ActorMaterializer    = ActorMaterializer()
  implicit val executionContext: ExecutionContext = system.dispatcher

  scala.sys.addShutdownHook {
    logger.info(s"${config.app.actorSystem} system is stopping...")
    system.terminate()
    Await.result(system.whenTerminated, 15.seconds)
    logger.info(s"${config.app.actorSystem} system terminated")
  }
}

trait DatabaseLayer extends ElasticsearchContext {

  this: Core =>

  private val properties = ElasticProperties(Seq(config.elastic.node))

  val esClient: ElasticClient = ElasticClient(properties)
}

trait ServiceLayer extends BookServiceComponent {

  this: DatabaseLayer with Core =>

  val bookService: BookService = new BookServiceImpl
}

trait EventHandlerLayer extends EventHandlerComponent {

  this: ServiceLayer with Core =>

  val eventHandler: ActorRef = system.actorOf(EventHandler.apply, EventHandler.name)
}

