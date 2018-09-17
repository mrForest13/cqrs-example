package com.cqrs.write.service

import com.cqrs.common.error.ConflictException
import com.cqrs.write.Core
import com.cqrs.write.db.dao.component.CategoryDaoComponent
import com.cqrs.write.db.model.Category
import com.cqrs.write.db.{DatabaseContext, HasJdbcProfile}

import scala.concurrent.Future

trait CategoryService {
  def add(category: Category): Future[Unit]
}

trait CategoryServiceComponent {

  this: CategoryDaoComponent with DatabaseContext with HasJdbcProfile with Core =>

  val categoryService: CategoryService

  class CategoryServiceImpl extends CategoryService {

    import profile.api._

    def add(category: Category): Future[Unit] = {

      val action = for {
        _ <- categoryDao.findByName(category.name).map { elem =>
          if (elem.isDefined) throw ConflictException(s"Category already exist")
        }
        _ <- categoryDao.insert(category)
      } yield ()

      db.run(action.transactionally)
    }
  }
}
