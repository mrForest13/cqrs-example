package com.cqrs.example.handler.model

import com.cqrs.example.http.model.BookSearchParams

sealed trait Query

final case class GetBooks(searchParams: BookSearchParams) extends Query
