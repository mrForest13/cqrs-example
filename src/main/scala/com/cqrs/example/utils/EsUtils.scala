package com.cqrs.example.utils

import com.sksamuel.elastic4s.http.ElasticDsl._
import com.sksamuel.elastic4s.http.{RequestFailure, RequestSuccess, Response}
import com.sksamuel.elastic4s.searches.queries.RegexQuery
import com.sksamuel.elastic4s.searches.queries.matches.MatchQuery

import scala.concurrent.{ExecutionContext, Future}

object EsUtils {

  type regex = String => String

  def optionMatchQuery(field: String, value: Option[Any]): Option[MatchQuery] = {
    value.map(matchQuery(field, _))
  }

  def optionRegexQuery(field: String, value: Option[String], f: regex): Option[RegexQuery] = {
    value.map(value => regexQuery(field, f(value.toLowerCase)))
  }

  implicit class ResponseHelper[U](response: Future[Response[U]]) {

    implicit def getResponse(implicit ex: ExecutionContext): Future[U] = response.map {
      case RequestSuccess(_, _, _, result) => result
      case RequestFailure(_, _, _, result) => throw EsException(result.reason)
    }
  }
}
