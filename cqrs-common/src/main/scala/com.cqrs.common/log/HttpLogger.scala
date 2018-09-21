package com.cqrs.common.log

import akka.http.scaladsl.server.Directives.mapRequest
import akka.http.scaladsl.server.{Directive0, Directives}
import com.typesafe.scalalogging.LazyLogging

object RequestLogger extends LazyLogging {

  def log: Directive0 = {
    mapRequest { request =>
      logger.info(s"""
           |Method: ${request.method}
           |Path: ${request.uri.path}
           |Query: ${request.uri.rawQueryString}
           |Content-type: ${request.entity.contentType}
           |Headers: ${request.headers}""".stripMargin)
      request
    }
  }
}

object ResponseLogger extends Directives with LazyLogging {

  def log: Directive0 = extractRequestContext.flatMap { ctx =>
    val start = System.currentTimeMillis()
    mapResponse { resp =>
      val took = System.currentTimeMillis() - start
      logger.info(s"""
           |Method: ${ctx.request.method}
           |Path: ${ctx.request.uri}
           |Status: ${resp.status.intValue}
           |Headers: ${resp.headers}
           |Duration: $took ms
           |Content-type: ${resp.entity.contentType}
           |Body: ${resp.entity}""".stripMargin)
      resp
    }
  }
}
