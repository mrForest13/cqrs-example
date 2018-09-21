package com.cqrs.write.service

import com.cqrs.common.error.ConflictException
import com.cqrs.write.Core
import com.cqrs.write.db.dao.component.CategoryDaoComponent
import com.cqrs.write.db.model.Category
import com.cqrs.write.db.{DatabaseContext, HasJdbcProfile, Id}

import scala.concurrent.Future

trait CategoryService {
  def add(category: Category): Future[Id[Category]]
}

trait CategoryServiceComponent {

  this: CategoryDaoComponent with DatabaseContext with HasJdbcProfile with Core =>

  val categoryService: CategoryService

  class CategoryServiceImpl extends CategoryService {

    import profile.api._

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
}
