package com.cqrs.write.db.model

import java.sql.Timestamp

import com.cqrs.write.db.{Entity, Id}
import com.cqrs.write.http.model.AuthorContent

final case class Author(
  id: Option[Id[Author]],
  firstName: String,
  lastName: String,
  updatedDate: Option[Timestamp] = None,
  createdDate: Option[Timestamp] = None)
    extends Entity[Author] {

  def this(content: AuthorContent) {
    this(None, content.firstName, content.lastName)
  }
}

object Author {

  type dbRow = (Option[Id[Author]], String, String, Option[Timestamp], Option[Timestamp])

  val construct: dbRow => Author = {
    case (id, firstName, lastName, _, _) => Author(id, firstName, lastName, None, None)
  }
}
