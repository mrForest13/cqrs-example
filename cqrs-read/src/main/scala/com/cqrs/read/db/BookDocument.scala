package com.cqrs.read.db

import com.cqrs.event.AddNewBookEvent
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

  def this(event: AddNewBookEvent) {
    this(
      event.title,
      event.author,
      event.publisher,
      event.language,
      event.category,
      event.description
    )
  }
}

object BookDocument {

  val indexName: String   = "book-store"
  val mappingName: String = "book"

  implicit val formatter: RootJsonFormat[BookDocument] = jsonFormat6(BookDocument.apply)
}
