package com.cqrs.example

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import com.cqrs.example.config.{AppConfig, Config}
import com.cqrs.example.db.dao.component._
import com.cqrs.example.db.{DatabaseContext, HasJdbcProfile}
import com.cqrs.example.handler.CommandHandlerComponent
import com.cqrs.example.http.error.ExceptionHandlerDirective._
import com.cqrs.example.http.{CommandRestApi, RestApi}
import com.cqrs.example.service._
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

trait DataBaseLayer
    extends HasJdbcProfile
    with DatabaseContext
    with AuthorDaoComponent
    with CategoryDaoComponent
    with BookDaoComponent {

  this: Core =>

  val databaseConfig: DatabaseConfig[JdbcProfile] =
    DatabaseConfig.forConfig[JdbcProfile]("mysql", ConfigFactory.load(config.app.dbConfigFile))

  val profile = databaseConfig.profile

  import profile.api._

  lazy val db: Database = databaseConfig.db

  val authorDao: AuthorDao     = new AuthorDao()
  val categoryDao: CategoryDao = new CategoryDao()
  val bookDao: BookDao         = new BookDao()
}

trait ServiceLayer
    extends AuthorServiceComponent
    with CategoryServiceComponent
    with BookServiceComponent {

  this: DataBaseLayer with Core =>

  val authorService: AuthorService     = new AuthorServiceImpl
  val categoryService: CategoryService = new CategoryServiceImpl
  val bookService: BookService         = new BookServiceImpl
}

trait HandlerLayer extends CommandHandlerComponent {

  this: ServiceLayer with Core =>

  val commandHandler: ActorRef = system.actorOf(CommandHandler.apply, CommandHandler.name)
}

trait RestApiLayer extends RestApi {

  this: HandlerLayer =>

  val commandRestApi: RestApi = new CommandRestApi(commandHandler)

  val routes: Route = handleExceptions(exceptionHandler) {
    Seq(
      commandRestApi
    ).map(_.routes)
      .reduceLeft(_ ~ _)
  }
}
