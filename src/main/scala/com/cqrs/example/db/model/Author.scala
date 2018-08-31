package com.cqrs.example.db.model

import com.cqrs.example.db.{HasId, Id}

final case class Author(id: Option[Id[Author]], firstName: String, lastName: String)
    extends HasId[Author]
