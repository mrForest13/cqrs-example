package com.cqrs.example.es

import com.cqrs.example.db.`type`.Language
import com.cqrs.example.db.model.{Author, Book, Category}
import spray.json._
import spray.json.DefaultJsonProtocol._

final case class BookDocument(
  title: String,
  author: String,
  publisher: String,
  language: Language,
  category: String,
  description: String) {

  def this(book: Book, author: Author, category: Category) {
    this(
      book.title,
      author.firstName + " " + author.lastName,
      book.publisher,
      book.language,
      category.name,
      book.description
    )
  }
}

object BookDocument {

  val indexName: String   = "book-store"
  val mappingName: String = "book"

  implicit val formatter: RootJsonFormat[BookDocument] = jsonFormat6(BookDocument.apply)
}
