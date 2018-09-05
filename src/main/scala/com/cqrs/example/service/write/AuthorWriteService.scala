package com.cqrs.example.service.write

import com.cqrs.example.db.dao.component.AuthorDaoComponent
import com.cqrs.example.db.model.Author
import com.cqrs.example.Core
import com.cqrs.example.db.DatabaseContext

import scala.concurrent.Future

trait AuthorWriteService {
  def add(author: Author): Future[Unit]
}

trait AuthorWriteServiceComponent {

  this: AuthorDaoComponent with DatabaseContext with Core =>

  val authorWriteService: AuthorWriteService

  class AuthorWriteServiceImpl extends AuthorWriteService {

    def add(author: Author): Future[Unit] = {
      db.run {
        authorDao.insert(author).map(_ => ())
      }
    }
  }
}
