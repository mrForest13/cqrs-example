package com.cqrs.example.http

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.{Directives, Route}
import akka.util.Timeout
import scala.concurrent.duration._

trait RestApi extends Directives with SprayJsonSupport {

  implicit val timeout: Timeout = Timeout(15.second)

  def routes: Route
}
