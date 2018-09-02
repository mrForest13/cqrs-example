package com.cqrs.example.utils

import com.sksamuel.elastic4s.http.ElasticDsl._
import com.sksamuel.elastic4s.http.{RequestFailure, RequestSuccess, Response}
import com.sksamuel.elastic4s.searches.queries.matches.MatchQuery
import com.typesafe.scalalogging.Logger

import scala.concurrent.{ExecutionContext, Future}

object EsUtils {

  def optionMatchQuery(field: String, value: Option[Any]): Option[MatchQuery] = {
    value.map(matchQuery(field, _))
  }

  def logResponse[U](logger: Logger, customMessage: String)(response: Future[Response[U]])(
    implicit ex: ExecutionContext): Future[Response[U]] = response.map {
    case res @ RequestSuccess(status, _, _, _) =>
      logger.debug(s"Success: $customMessage. Status code: $status"); res
    case res @ RequestFailure(status, _, _, _) =>
      logger.error(s"Failure: $customMessage. Status code: $status"); res
  }
}
