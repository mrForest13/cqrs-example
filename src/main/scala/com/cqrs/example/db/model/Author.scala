package com.cqrs.example.db.model

import com.cqrs.example.db.{HasId, Id}
import com.cqrs.example.http.model.AuthorContent

final case class Author(id: Option[Id[Author]], firstName: String, lastName: String)
    extends HasId[Author] {

  def this(content: AuthorContent) {
    this(None, content.firstName, content.lastName)
  }
}
