package com.cqrs.write.utils

import com.cqrs.common.event.NewBookAddedEvent
import com.cqrs.write.db.model.{Author, Book, Category}

object EventFactory {

  def newBookAdded(book: Book, author: Author, category: Category): NewBookAddedEvent = {

    require(book.id.isDefined, "Book id cannot be empty!")

    NewBookAddedEvent(
      id = book.id.get.value.toString,
      title = book.title,
      author = author.firstName + " " + author.lastName,
      publisher = book.publisher,
      language = book.language.name,
      category = category.name,
      description = book.description
    )
  }
}
