package com.cqrs.event

sealed trait Event

final case class AddNewBookEvent(
  title: String,
  author: String,
  publisher: String,
  language: String,
  category: String,
  description: String)
    extends Event
