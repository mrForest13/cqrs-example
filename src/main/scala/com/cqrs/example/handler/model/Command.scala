package com.cqrs.example.handler.model

import com.cqrs.example.db.Id
import com.cqrs.example.db.model.Author
import com.cqrs.example.http.model.{AuthorContent, BookContent, CategoryContent}

sealed trait Command[T] {
  def content: T
}

final case class AddAuthor(content: AuthorContent)                   extends Command[AuthorContent]
final case class AddCategory(content: CategoryContent)               extends Command[CategoryContent]
final case class AddBook(authorId: Id[Author], content: BookContent) extends Command[BookContent]
