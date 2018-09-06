package com.cqrs.example.db.model

import java.sql.Timestamp

import com.cqrs.example.db.{Entity, Id}
import com.cqrs.example.db.`type`.Language
import com.cqrs.example.http.model.BookContent

final case class Book(
  id: Option[Id[Book]],
  title: String,
  authorId: Id[Author],
  publisher: String,
  language: Language,
  categoryId: Id[Category],
  description: String,
  updatedDate: Option[Timestamp] = None,
  createdDate: Option[Timestamp] = None)
    extends Entity[Book] {

  def this(authorId: Id[Author], content: BookContent) = {
    this(None,
         content.title,
         authorId,
         content.publisher,
         content.language,
         content.categoryId,
         content.description)
  }
}

object Book {

  type dbRow = (
    Option[Id[Book]],
    String,
    Id[Author],
    String,
    Language,
    Id[Category],
    String,
    Option[Timestamp],
    Option[Timestamp])

  val construct: dbRow => Book = {
    case (id, title, authorId, publisher, language, categoryId, description, _, _) =>
      Book(id, title, authorId, publisher, language, categoryId, description, None, None)
  }
}
