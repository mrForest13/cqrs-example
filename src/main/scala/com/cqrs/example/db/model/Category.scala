package com.cqrs.example.db.model

import com.cqrs.example.db.{HasId, Id}

final case class Category(id: Option[Id[Category]], name: String) extends HasId[Category]
