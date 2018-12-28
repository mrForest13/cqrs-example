package com.cqrs.write

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.server.{Route, RouteConcatenation}
import akka.routing.FromConfig
import akka.stream.ActorMaterializer
import ch.megard.akka.http.cors.scaladsl.CorsDirectives.cors
import com.cqrs.write.config.{AppConfig, Config}
import com.cqrs.write.db.dao.{AuthorDao, BookDao, CategoryDao}
import com.cqrs.write.db.{DatabaseContext, HasJdbcProfile}
import com.cqrs.write.handler._
import com.cqrs.write.http._
import com.cqrs.write.http.error.ExceptionHandlerDirective._
import com.cqrs.write.service._
import com.google.common.net.HostAndPort
import com.softwaremill.macwire._
import com.softwaremill.macwire.akkasupport._
import com.softwaremill.tagging._
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.StrictLogging
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

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

trait DatabaseLayer extends HasJdbcProfile with DatabaseContext {

  this: Core =>

  val databaseConfig: DatabaseConfig[JdbcProfile] =
    DatabaseConfig.forConfig[JdbcProfile]("slick", ConfigFactory.load("database"))

  val profile = databaseConfig.profile

  import profile.api._

  implicit val db: Database = databaseConfig.db

  val authorDao: AuthorDao     = wire[AuthorDao]
  val categoryDao: CategoryDao = wire[CategoryDao]
  val bookDao: BookDao         = wire[BookDao]

  scala.sys.addShutdownHook {
    db.close()
  }
}

trait EventHandlerLayer {

  this: Core =>

  private val routeName: String = BookEventHandler.routeName

  val bookEventHandler: ActorRef @@ BookEventHandler =
    system.actorOf(FromConfig.props(), routeName).taggedWith[BookEventHandler]
}

trait ServiceLayer {

  this: DatabaseLayer with EventHandlerLayer with Core =>

  val authorService: AuthorService     = wire[AuthorServiceImpl]
  val categoryService: CategoryService = wire[CategoryServiceImpl]
  val bookService: BookService         = wire[BookServiceImpl]
}

trait CommandHandlerLayer {

  this: ServiceLayer with Core =>

  val authorCommandHandler: ActorRef @@ AuthorCommandHandler =
    wireActor[AuthorCommandHandler](AuthorCommandHandler.name).taggedWith[AuthorCommandHandler]
  val categoryCommandHandler: ActorRef @@ CategoryCommandHandler =
    wireActor[CategoryCommandHandler](CategoryCommandHandler.name).taggedWith[CategoryCommandHandler]
  val bookCommandHandler: ActorRef @@ BookCommandHandler =
    wireActor[BookCommandHandler](BookCommandHandler.name).taggedWith[BookCommandHandler]
}

trait ApiLayer extends Api with RouteConcatenation {

  this: CommandHandlerLayer with Core =>

  private val host: String = config.http.host
  private val port: Int    = config.http.port

  val apiAddress: HostAndPort = HostAndPort.fromParts(host, port)

  val swaggerApi: Api = wire[SwaggerApi]

  val authorApi: Api   = wire[AuthorApi]
  val categoryApi: Api = wire[CategoryApi]
  val bookApi: Api     = wire[BookApi]

  val routes: Route = cors() {
    handleExceptions(exceptionHandler) {
      Seq(
        authorApi,
        categoryApi,
        bookApi,
        swaggerApi
      ).map(_.routes)
        .reduceLeft(_ ~ _)
    }
  }
}
