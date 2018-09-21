package com.cqrs.write.db.model

import java.sql.Timestamp

import com.cqrs.write.db.{Entity, Id}
import com.cqrs.write.http.model.CategoryContent

final case class Category(
  id: Option[Id[Category]],
  name: String,
  updatedDate: Option[Timestamp] = None,
  createdDate: Option[Timestamp] = None)
    extends Entity[Category] {

  def this(content: CategoryContent) {
    this(None, content.name)
  }
}

object Category {

  type dbRow = (Option[Id[Category]], String, Option[Timestamp], Option[Timestamp])

  val construct: dbRow => Category = {
    case (id, name, _, _) => Category(id, name, None, None)
  }
}
