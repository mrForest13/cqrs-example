package com.cqrs.event.utils

import com.cqrs.common.error.EsException
import com.sksamuel.elastic4s.http.{RequestFailure, RequestSuccess, Response}

import scala.concurrent.{ExecutionContext, Future}

object EsUtils {

  implicit class ResponseHelper[U](response: Future[Response[U]]) {

    implicit def getResponse(implicit ex: ExecutionContext): Future[U] = response.map {
      case RequestSuccess(_, _, _, result) => result
      case RequestFailure(_, _, _, result) => throw EsException(result.reason)
    }
  }
}
