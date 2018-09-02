package com.cqrs.example.db.model

import com.cqrs.example.db.{HasId, Id}
import com.cqrs.example.db.`type`.Language
import com.cqrs.example.http.model.BookContent

final case class Book(
  id: Option[Id[Book]],
  title: String,
  authorId: Id[Author],
  publisher: String,
  language: Language,
  categoryId: Id[Category],
  description: String)
    extends HasId[Book] {

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
