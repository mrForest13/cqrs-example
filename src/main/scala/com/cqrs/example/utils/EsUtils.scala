package com.cqrs.example.utils

import com.sksamuel.elastic4s.http.ElasticDsl._
import com.sksamuel.elastic4s.http.{RequestFailure, RequestSuccess, Response}
import com.sksamuel.elastic4s.searches.queries.matches.MatchQuery

import scala.concurrent.Future

object EsUtils {

  def optionMatchQuery(field: String, value: Option[Any]): Option[MatchQuery] = {
    value.map(matchQuery(field, _))
  }

  implicit class ResponseHelper[U](response: Future[Response[U]]) {

    implicit def getResponse: Future[U] = response.map {
      case RequestSuccess(_, _, _, result) => result
      case RequestFailure(_, _, _, result) => throw EsException(result.reason)
    }
  }
}
