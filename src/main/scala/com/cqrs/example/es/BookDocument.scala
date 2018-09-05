package com.cqrs.example.es

import com.cqrs.example.db.model.{Author, Book, Category}
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

  def this(book: Book, author: Author, category: Category) {
    this(
      book.title,
      author.firstName + " " + author.lastName,
      book.publisher,
      book.language.name,
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
