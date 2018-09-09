package com.cqrs.example.handler.model

import com.cqrs.example.es.BookDocument

sealed trait Event

final case class InsertBookToReadDb(book: BookDocument) extends Event
