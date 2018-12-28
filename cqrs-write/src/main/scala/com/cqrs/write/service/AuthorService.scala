package com.cqrs.write.service

import com.cqrs.write.db.`type`.Id
import com.cqrs.write.db.dao.AuthorDao
import com.cqrs.write.db.model.Author
import slick.jdbc.JdbcBackend

import scala.concurrent.Future

trait AuthorService {
  def add(author: Author): Future[Id[Author]]
}

class AuthorServiceImpl(authorDao: AuthorDao)(implicit db: JdbcBackend#Database) extends AuthorService {

  def add(author: Author): Future[Id[Author]] = {
    db.run {
      authorDao.insert(author)
    }
  }
}
