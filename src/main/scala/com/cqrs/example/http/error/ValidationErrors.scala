package com.cqrs.example.http.error

import io.swagger.annotations.ApiModel
import spray.json.DefaultJsonProtocol.jsonFormat1
import spray.json.RootJsonFormat
import spray.json.DefaultJsonProtocol._

@ApiModel(value = "Validation errors")
final case class ValidationErrors(
  desc: Map[String, String])

object ValidationErrors {
  implicit val formatter: RootJsonFormat[ValidationErrors] = jsonFormat1(ValidationErrors.apply)
}