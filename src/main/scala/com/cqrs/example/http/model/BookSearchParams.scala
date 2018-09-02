package com.cqrs.example.http.model

final case class BookSearchParams(
  title: Option[String],
  author: Option[String],
  publisher: Option[String],
  category: Option[String])
