package com.cqrs.read.handler.model

import com.cqrs.read.http.model.BookSearchParams

sealed trait Query

final case class GetBooks(searchParams: BookSearchParams) extends Query
