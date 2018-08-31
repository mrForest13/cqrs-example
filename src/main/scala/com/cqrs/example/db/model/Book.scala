package com.cqrs.example.db.model

import com.cqrs.example.db.{HasId, Id}
import com.cqrs.example.db.`type`.Language

final case class Book(
  id: Option[Id[Book]],
  title: String,
  authorId: Id[Author],
  publisher: String,
  language: Language,
  categoryId: Id[Category],
  description: String)
    extends HasId[Book]
