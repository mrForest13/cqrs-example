package com.cqrs.read.db

import com.cqrs.common.event.BookAddedEvent
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
  description: String) {}

object BookDocument {

  def apply(e: BookAddedEvent): BookDocument =
    new BookDocument(e.title, e.author, e.publisher, e.language, e.category, e.description)

  val indexName: String = "book"

  implicit val formatter: RootJsonFormat[BookDocument] = jsonFormat6(BookDocument.apply)
}
