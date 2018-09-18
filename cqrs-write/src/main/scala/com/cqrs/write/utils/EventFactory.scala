package com.cqrs.write.utils

import com.cqrs.common.event.AddNewBook
import com.cqrs.write.db.model.{Author, Book, Category}

object EventFactory {

  def addNewBook(book: Book, author: Author, category: Category): AddNewBook = {
    AddNewBook(
      title = book.title,
      author = author.firstName + " " + author.lastName,
      publisher = book.publisher,
      language = book.language.name,
      category = category.name,
      description = book.description
    )
  }
}
