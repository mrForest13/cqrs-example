package com.cqrs.example.handler.model

import com.cqrs.example.db.Id
import com.cqrs.example.db.model.Author
import com.cqrs.example.http.model.{AuthorContent, BookContent, CategoryContent}

sealed trait Command

final case class AddAuthorCommand(content: AuthorContent)                   extends Command
final case class AddCategoryCommand(content: CategoryContent)               extends Command
final case class AddBookCommand(authorId: Id[Author], content: BookContent) extends Command
