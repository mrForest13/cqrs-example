package com.cqrs.example.http.model

import spray.json.DefaultJsonProtocol._
import spray.json.RootJsonFormat

final case class CategoryContent(name: String)

object CategoryContent {
  implicit val formatter: RootJsonFormat[CategoryContent] = jsonFormat1(CategoryContent.apply)
}