package com.cqrs.write.service

import com.cqrs.write.Core
import com.cqrs.write.db.{DatabaseContext, Id}
import com.cqrs.write.db.dao.component.AuthorDaoComponent
import com.cqrs.write.db.model.Author

import scala.concurrent.Future

trait AuthorService {
  def add(author: Author): Future[Id[Author]]
}

trait AuthorServiceComponent {

  this: AuthorDaoComponent with DatabaseContext with Core =>

  val authorService: AuthorService

  class AuthorServiceImpl extends AuthorService {

    def add(author: Author): Future[Id[Author]] = {
      db.run {
        authorDao.insert(author)
      }
    }
  }
}
