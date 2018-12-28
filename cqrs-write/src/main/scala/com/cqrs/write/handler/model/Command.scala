package com.cqrs.write.handler.model
import com.cqrs.write.db.`type`.Id
import com.cqrs.write.db.model.Author
import com.cqrs.write.http.model.{AuthorContent, BookContent, CategoryContent}

sealed trait Command

final case class AddAuthorCommand(content: AuthorContent)                   extends Command
final case class AddCategoryCommand(content: CategoryContent)               extends Command
final case class AddBookCommand(authorId: Id[Author], content: BookContent) extends Command
