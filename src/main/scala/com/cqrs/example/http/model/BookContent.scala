package com.cqrs.example.http.model

import com.cqrs.example.db.Id
import com.cqrs.example.db.`type`.Language
import com.cqrs.example.db.model.Category
import spray.json.RootJsonFormat
import spray.json.DefaultJsonProtocol._

final case class BookContent(
  title: String,
  publisher: String,
  language: Language,
  categoryId: Id[Category],
  description: String)

object BookContent {
  implicit val formatter: RootJsonFormat[BookContent] = jsonFormat5(BookContent.apply)
}
