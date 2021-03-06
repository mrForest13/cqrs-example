package com.cqrs.common.event

sealed trait Event

final case class BookAddedEvent(
  id: String,
  title: String,
  author: String,
  publisher: String,
  language: String,
  category: String,
  description: String)
    extends Event
