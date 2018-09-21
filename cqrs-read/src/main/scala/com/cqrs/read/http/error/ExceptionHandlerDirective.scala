package com.cqrs.read.http.error

import java.util.concurrent.TimeoutException

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.{StatusCode, StatusCodes}
import akka.http.scaladsl.server.{Directives, ExceptionHandler, Route}
import com.typesafe.scalalogging.LazyLogging
import spray.json.DefaultJsonProtocol.jsonFormat1
import spray.json.RootJsonFormat
import spray.json.DefaultJsonProtocol._

final case class ErrorResponse(desc: String)

object ErrorResponse {

  val timeoutMsg: String = "Server is busy now. Please try again in sometime"

  implicit val formatter: RootJsonFormat[ErrorResponse] = jsonFormat1(ErrorResponse.apply)
}

object ExceptionHandlerDirective extends Directives with SprayJsonSupport with LazyLogging {

  val exceptionHandler: ExceptionHandler = ExceptionHandler {
    case th: TimeoutException  => complete(StatusCodes.RequestTimeout, th, ErrorResponse.timeoutMsg)
    case th                    => failWith(th)
  }

  def complete(statusCode: StatusCode, th: Throwable, msg: String): Route = {
    logger.error(th.getMessage, th)
    complete((statusCode, ErrorResponse(msg)))
  }
}
