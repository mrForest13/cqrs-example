package com.cqrs.example

import akka.http.scaladsl.Http
import com.cqrs.example.utils.RequestLogger._

import scala.util.{Failure, Success}

object AppLauncher
    extends App
    with Core
    with BootedCore
    with WriteDatabaseLayer
    with ReadDatabaseLayer
    with ReadServiceLayer
    with EventHandlerLayer
    with WriteServiceLayer
    with CommandHandlerLayer
    with QueryHandlerLayer
    with RestApiLayer {

  logger.info(s"Initializing REST api ...]")

  val bindingFuture = Http().bindAndHandle(log(routes), host, port)

  bindingFuture.onComplete {
    case Failure(th) =>
      logger.error(th.getMessage, th)
      system.terminate()

    case Success(res) => logger.info(s"REST api started on ${res.localAddress}")
  }
}
