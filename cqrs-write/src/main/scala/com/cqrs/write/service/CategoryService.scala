package com.cqrs.write.service

import com.cqrs.common.error.ConflictException
import com.cqrs.write.db.`type`.Id
import com.cqrs.write.db.dao.CategoryDao
import com.cqrs.write.db.model.Category
import slick.jdbc.JdbcBackend

import scala.concurrent.{ExecutionContext, Future}

trait CategoryService {
  def add(category: Category): Future[Id[Category]]
}

class CategoryServiceImpl(categoryDao: CategoryDao)(implicit db: JdbcBackend#Database, ex: ExecutionContext)
    extends CategoryService {

  import categoryDao.profile.api._

  def add(category: Category): Future[Id[Category]] = {

    val action = for {
      _ <- categoryDao.findByName(category.name).map { elem =>
        if (elem.isDefined) throw ConflictException(s"Category already exist")
      }
      id <- categoryDao.insert(category)
    } yield id

    db.run(action.transactionally)
  }
}
