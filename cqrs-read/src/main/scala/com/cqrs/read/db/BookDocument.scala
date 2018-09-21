package com.cqrs.read.db

import io.swagger.annotations.ApiModel
import spray.json._
import spray.json.DefaultJsonProtocol._

@ApiModel(value = "Book basic information")
final case class BookDocument(
  title: String,
  author: String,
  publisher: String,
  language: String,
  category: String,
  description: String) {
}

object BookDocument {

  val indexName: String   = "book"

  implicit val formatter: RootJsonFormat[BookDocument] = jsonFormat6(BookDocument.apply)
}
