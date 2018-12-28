package com.cqrs.write.utils

import com.cqrs.common.event.BookAddedEvent
import com.cqrs.write.db.model.{Author, Book, Category}

object EventFactory {

  def bookAdded(book: Book, author: Author, category: Category): BookAddedEvent = {

    require(book.id.isDefined, "Book id cannot be empty!")

    BookAddedEvent(
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
