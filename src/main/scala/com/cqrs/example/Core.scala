package com.cqrs.example

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.server.{Route, RouteConcatenation}
import akka.stream.ActorMaterializer
import com.cqrs.example.config.{AppConfig, Config}
import com.cqrs.example.db.dao.component._
import com.cqrs.example.db.{DatabaseContext, HasJdbcProfile}
import com.cqrs.example.es.ElasticsearchContext
import com.cqrs.example.handler._
import com.cqrs.example.http.error.ExceptionHandlerDirective._
import ch.megard.akka.http.cors.scaladsl.CorsDirectives.cors
import com.cqrs.example.http.{ReadSideRestApi, RestApi, SwaggerDocRestApi, WriteSideRestApi}
import com.cqrs.example.service.read.{BookReadService, BookReadServiceComponent}
import com.cqrs.example.service.write._
import com.sksamuel.elastic4s.http.{ElasticClient, ElasticProperties}
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

trait ReadDatabaseLayer extends ElasticsearchContext {

  this: Core =>

  private val properties = ElasticProperties(Seq(config.elastic.node))

  val esClient: ElasticClient = ElasticClient(properties)
}

trait ReadServiceLayer extends BookReadServiceComponent {

  this: ReadDatabaseLayer with Core =>

  val bookReadService: BookReadService = new BookReadServiceImpl
}

trait EventHandlerLayer extends EventHandlerComponent {

  this: ReadServiceLayer with Core =>

  val eventHandler: ActorRef = system.actorOf(EventHandler.apply, EventHandler.name)
}

trait WriteServiceLayer
    extends AuthorWriteServiceComponent
    with CategoryWriteServiceComponent
    with BookWriteServiceComponent {

  this: WriteDatabaseLayer with EventHandlerLayer with Core =>

  val authorWriteService: AuthorWriteService     = new AuthorWriteServiceImpl
  val categoryWriteService: CategoryWriteService = new CategoryWriteServiceImpl
  val bookWriteService: BookWriteService         = new BookWriteServiceImpl
}

trait CommandHandlerLayer extends CommandHandlerComponent {

  this: WriteServiceLayer with Core =>

  val commandHandler: ActorRef = system.actorOf(CommandHandler.apply, CommandHandler.name)
}

trait QueryHandlerLayer extends QueryHandlerComponent {

  this: ReadServiceLayer with Core =>

  val queryHandler: ActorRef = system.actorOf(QueryHandler.apply, QueryHandler.name)
}

trait RestApiLayer extends RestApi with RouteConcatenation {

  this: CommandHandlerLayer with QueryHandlerLayer with Core =>

  val host: String = config.http.host
  val port: Int    = config.http.port

  val swaggerDocRestApi: RestApi = new SwaggerDocRestApi(host, port)

  val writeSideRestApi: RestApi = new WriteSideRestApi(commandHandler)
  val readSideRestApi: RestApi  = new ReadSideRestApi(queryHandler)

  val routes: Route = cors() {
    handleExceptions(exceptionHandler) {
      Seq(
        writeSideRestApi,
        readSideRestApi,
        swaggerDocRestApi
      ).map(_.routes)
        .reduceLeft(_ ~ _)
    }
  }
}
