package com.cqrs.example

import akka.http.scaladsl.Http
import com.cqrs.example.utils.RequestLogger._

import scala.util.{Failure, Success}

object AppLauncher
    extends App
    with Core
    with BootedCore
    with DataBaseLayer
    with ServiceLayer
    with HandlerLayer
    with RestApiLayer {

  logger.info(s"Initializing REST api ...]")

  val host: String = config.http.host
  val port: Int    = config.http.port

  val bindingFuture = Http().bindAndHandle(log(routes), host, port)

  bindingFuture.onComplete {
    case Failure(th) =>
      logger.error(th.getMessage, th)
      system.terminate()

    case Success(res) => logger.info(s"REST api started on ${res.localAddress}")
  }
}
