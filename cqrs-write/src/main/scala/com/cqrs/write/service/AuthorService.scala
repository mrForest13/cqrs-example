package com.cqrs.write.service

import com.cqrs.write.Core
import com.cqrs.write.db.DatabaseContext
import com.cqrs.write.db.dao.component.AuthorDaoComponent
import com.cqrs.write.db.model.Author

import scala.concurrent.Future

trait AuthorService {
  def add(author: Author): Future[Unit]
}

trait AuthorServiceComponent {

  this: AuthorDaoComponent with DatabaseContext with Core =>

  val authorService: AuthorService

  class AuthorServiceImpl extends AuthorService {

    def add(author: Author): Future[Unit] = {
      db.run {
        authorDao.insert(author).map(_ => ())
      }
    }
  }
}
