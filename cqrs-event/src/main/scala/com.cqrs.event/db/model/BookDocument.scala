package com.cqrs.event.db.model

import com.cqrs.common.event.NewBookAddedEvent
import spray.json.RootJsonFormat
import spray.json.DefaultJsonProtocol._

final case class BookDocument(
  title: String,
  author: String,
  publisher: String,
  language: String,
  category: String,
  description: String)

object BookDocument {

  def apply(e: NewBookAddedEvent): BookDocument =
    new BookDocument(e.title, e.author, e.publisher, e.language, e.category, e.description)

  val indexName: String = "book"

  implicit val formatter: RootJsonFormat[BookDocument] = jsonFormat6(BookDocument.apply)
}
