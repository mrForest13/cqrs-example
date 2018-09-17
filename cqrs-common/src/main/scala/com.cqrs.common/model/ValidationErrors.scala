package com.cqrs.common.model

import spray.json.DefaultJsonProtocol.{jsonFormat1, _}
import spray.json.RootJsonFormat

final case class ValidationErrors(
  desc: Map[String, String])

object ValidationErrors {
  implicit val formatter: RootJsonFormat[ValidationErrors] = jsonFormat1(ValidationErrors.apply)
}