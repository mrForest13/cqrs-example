package com.cqrs.example.http.model

import spray.json.DefaultJsonProtocol._
import spray.json.RootJsonFormat

final case class AuthorContent(firstName: String, lastName: String)

object AuthorContent {
  implicit val formatter: RootJsonFormat[AuthorContent] = jsonFormat2(AuthorContent.apply)
}
