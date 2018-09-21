package com.cqrs.read

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.server.{Route, RouteConcatenation}
import akka.stream.ActorMaterializer
import ch.megard.akka.http.cors.scaladsl.CorsDirectives.cors
import com.cqrs.read.service.BookService
import com.cqrs.read.config.{AppConfig, Config}
import com.cqrs.read.db.ElasticsearchContext
import com.cqrs.read.handler._
import com.cqrs.read.http.error.ExceptionHandlerDirective._
import com.cqrs.read.http.{BookRestApi, RestApi, SwaggerDocRestApi}
import com.cqrs.read.service.BookServiceComponent
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

trait QueryHandlerLayer extends QueryHandlerComponent {

  this: ServiceLayer with Core =>

  val queryHandler: ActorRef = system.actorOf(QueryHandler.apply, QueryHandler.name)
}

trait RestApiLayer extends RestApi with RouteConcatenation {

  this: QueryHandlerLayer with Core =>

  val host: String = config.http.host
  val port: Int    = config.http.port

  val swaggerDocRestApi: RestApi = new SwaggerDocRestApi(host, port)

  val bookRestApi: RestApi  = new BookRestApi(queryHandler)

  val routes: Route = cors() {
    handleExceptions(exceptionHandler) {
      Seq(
        bookRestApi,
        swaggerDocRestApi
      ).map(_.routes)
        .reduceLeft(_ ~ _)
    }
  }
}
