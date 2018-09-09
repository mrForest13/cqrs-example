package com.cqrs.example

import com.cqrs.example.db.Id
import com.cqrs.example.db.`type`.Language
import com.cqrs.example.http.model.{AuthorContent, BookContent, CategoryContent}

object ExampleObject {

  val pl: Language = Language.fromCode("PL").get

  val authorContent: AuthorContent = AuthorContent(firstName = "Jan", lastName = "Nowak")

  val categoryContent: CategoryContent = CategoryContent(name = "Horror")

  val bookContent: BookContent =
    BookContent(
      title = "example title",
      publisher = "example publisher",
      language = pl,
      categoryId = Id(1),
      description = "example description"
    )
}
