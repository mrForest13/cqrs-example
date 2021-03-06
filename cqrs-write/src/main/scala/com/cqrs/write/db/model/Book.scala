package com.cqrs.write.db.model

import java.sql.Timestamp

import com.cqrs.write.db.BaseEntity
import com.cqrs.write.db.`type`.{Id, Language}
import com.cqrs.write.http.model.BookContent

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
    extends BaseEntity[Book] {

  def this(authorId: Id[Author], content: BookContent) = {
    this(None, content.title, authorId, content.publisher, content.language, content.categoryId, content.description)
  }

  def withId(id: Id[Book]): Book = this.copy(id = Some(id))
}

object Book {

  type dbRow =
    (Option[Id[Book]], String, Id[Author], String, Language, Id[Category], String, Option[Timestamp], Option[Timestamp])

  val construct: dbRow => Book = {
    case (id, title, authorId, publisher, language, categoryId, description, _, _) =>
      Book(id, title, authorId, publisher, language, categoryId, description, None, None)
  }
}
