package com.cqrs.write

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.server.{Route, RouteConcatenation}
import akka.stream.ActorMaterializer
import ch.megard.akka.http.cors.scaladsl.CorsDirectives.cors
import com.cqrs.write.config.{AppConfig, Config}
import com.cqrs.write.db.dao.component._
import com.cqrs.write.db.{DatabaseContext, HasJdbcProfile}
import com.cqrs.write.handler._
import com.cqrs.write.http.error.ExceptionHandlerDirective._
import com.cqrs.write.http.{BookRestApi, RestApi, SwaggerDocRestApi}
import com.cqrs.write.service._
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

trait WriteDatabaseLayer
    extends HasJdbcProfile
    with DatabaseContext
    with AuthorDaoComponent
    with CategoryDaoComponent
    with BookDaoComponent {

  this: Core =>

  val databaseConfig: DatabaseConfig[JdbcProfile] =
    DatabaseConfig.forConfig[JdbcProfile]("slick", ConfigFactory.load(config.app.dbConfigFile))

  val profile = databaseConfig.profile

  import profile.api._

  val db: Database = databaseConfig.db

  val authorDao: AuthorDao     = new AuthorDao()
  val categoryDao: CategoryDao = new CategoryDao()
  val bookDao: BookDao         = new BookDao()
}

trait EventHandlerLayer extends EventHandlerComponent {

  this: Core =>

  val eventHandler: ActorRef = system.actorOf(EventHandler.apply, EventHandler.name)
}

trait WriteServiceLayer
    extends AuthorServiceComponent
    with CategoryServiceComponent
    with BookServiceComponent {

  this: WriteDatabaseLayer with EventHandlerLayer with Core =>

  val authorService: AuthorService     = new AuthorServiceImpl
  val categoryService: CategoryService = new CategoryServiceImpl
  val bookService: BookService         = new BookServiceImpl
}

trait CommandHandlerLayer extends CommandHandlerComponent {

  this: WriteServiceLayer with Core =>

  val commandHandler: ActorRef = system.actorOf(CommandHandler.apply, CommandHandler.name)
}

trait RestApiLayer extends RestApi with RouteConcatenation {

  this: CommandHandlerLayer with Core =>

  val host: String = config.http.host
  val port: Int    = config.http.port

  val swaggerDocRestApi: RestApi = new SwaggerDocRestApi(host, port)

  val bookRestApi: RestApi = new BookRestApi(commandHandler)

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
