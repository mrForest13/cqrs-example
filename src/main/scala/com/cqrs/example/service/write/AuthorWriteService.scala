package com.cqrs.example.service.write

import com.cqrs.example.db.model.Author
import com.cqrs.example.{Core, WriteDatabaseLayer}

import scala.concurrent.Future

trait AuthorWriteService {
  def add(author: Author): Future[Unit]
}

trait AuthorWriteServiceComponent {

  this: WriteDatabaseLayer with Core =>

  val authorWriteService: AuthorWriteService

  class AuthorWriteServiceImpl extends AuthorWriteService {

    def add(author: Author): Future[Unit] = {
      db.run {
        authorDao.insert(author).map(_ => ())
      }
    }
  }
}
