package com.cqrs.example.http

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.{Directives, Route}
import com.typesafe.scalalogging.LazyLogging

trait RestApi extends Directives with SprayJsonSupport with LazyLogging {

  def routes: Route
}
