package com.cqrs.write

import akka.http.scaladsl.Http
import com.cqrs.common.log.RequestLogger._

import scala.util.{Failure, Success}

object AppLauncher
    extends App
    with Core
    with BootedCore
    with DatabaseLayer
    with EventHandlerLayer
    with ServiceLayer
    with CommandHandlerLayer
    with ApiLayer {

  logger.info(s"Initializing REST api ...]")

  val bindingFuture = Http().bindAndHandle(log(routes), apiAddress.getHost, apiAddress.getPort)

  bindingFuture.onComplete {
    case Failure(th) =>
      logger.error(th.getMessage, th)
      system.terminate()

    case Success(res) => logger.info(s"REST api started on ${res.localAddress}")
  }
}
