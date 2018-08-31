package com.cqrs.example.service

import com.cqrs.example.{Core, DataBaseLayer}
import com.cqrs.example.db.model.Author

import scala.concurrent.Future

trait AuthorService {
  def add(author: Author): Future[Unit]
}

trait AuthorServiceComponent {

  this: DataBaseLayer with Core =>

  val authorService: AuthorService

  class AuthorServiceImpl extends AuthorService {

    def add(author: Author): Future[Unit] = {
      db.run {
        authorDao.insert(author).map(_ => ())
      }
    }
  }
}
