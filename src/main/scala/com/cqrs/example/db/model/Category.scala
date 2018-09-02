package com.cqrs.example.db.model

import com.cqrs.example.db.{HasId, Id}
import com.cqrs.example.http.model.CategoryContent

final case class Category(id: Option[Id[Category]], name: String) extends HasId[Category] {

  def this(content: CategoryContent) {
    this(None, content.name)
  }
}
