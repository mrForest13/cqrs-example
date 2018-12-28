package com.cqrs.read

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.server.{Route, RouteConcatenation}
import akka.routing.FromConfig
import akka.stream.ActorMaterializer
import ch.megard.akka.http.cors.scaladsl.CorsDirectives.cors
import com.cqrs.read.service.{BookService, BookServiceImpl}
import com.cqrs.read.config.{AppConfig, Config}
import com.cqrs.read.handler._
import com.cqrs.read.http.error.ExceptionHandlerDirectives._
import com.cqrs.read.http.{Api, BookApi, SwaggerApi}
import com.google.common.net.HostAndPort
import com.sksamuel.elastic4s.http.{ElasticClient, ElasticProperties}
import com.typesafe.scalalogging.StrictLogging
import com.softwaremill.macwire._
import com.softwaremill.macwire.akkasupport._
import com.softwaremill.tagging._

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

trait DatabaseLayer {

  this: Core =>

  private val properties = ElasticProperties(Seq(config.elastic.node))

  val esClient: ElasticClient = ElasticClient(properties)

  scala.sys.addShutdownHook {
    esClient.close()
  }
}

trait ServiceLayer {

  this: DatabaseLayer with Core =>

  val bookService: BookService = wire[BookServiceImpl]
}

trait EventHandlerLayer {

  this: ServiceLayer with Core =>

  private val routeName: String = BookEventHandler.routeName

  private val bookEventProps = FromConfig.props(wireProps[BookEventHandler])

  val bookEventHandler: ActorRef @@ BookEventHandler =
    system.actorOf(bookEventProps, routeName).taggedWith[BookEventHandler]
}

trait QueryHandlerLayer {

  this: ServiceLayer with Core =>

  val queryHandler: ActorRef @@ BookQueryHandler =
    wireActor[BookQueryHandler](BookQueryHandler.name).taggedWith[BookQueryHandler]
}

trait ApiLayer extends Api with RouteConcatenation {

  this: QueryHandlerLayer with Core =>

  private val host: String = config.http.host
  private val port: Int    = config.http.port

  val apiAddress: HostAndPort = HostAndPort.fromParts(host, port)

  val swaggerApi: Api = wire[SwaggerApi]

  val bookRestApi: Api = wire[BookApi]

  val routes: Route = cors() {
    handleExceptions(exceptionHandler) {
      Seq(
        bookRestApi,
        swaggerApi
      ).map(_.routes)
        .reduceLeft(_ ~ _)
    }
  }
}
