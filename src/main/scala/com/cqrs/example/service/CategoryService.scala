package com.cqrs.example.service

import com.cqrs.example.db.model.Category
import com.cqrs.example.{Core, DataBaseLayer}
import com.cqrs.example.utils.ConflictException

import scala.concurrent.Future

trait CategoryService {
  def add(category: Category): Future[Unit]
}

trait CategoryServiceComponent {

  this: DataBaseLayer with Core =>

  val categoryService: CategoryService

  class CategoryServiceImpl extends CategoryService {

    import profile.api._

    def add(category: Category): Future[Unit] = {

      val action = for {
        _ <- categoryDao.findByName(category.name).map { elem =>
          if (elem.isDefined) throw ConflictException(s"Category already exists")
        }
        _ <- categoryDao.insert(category)
      } yield ()

      db.run(action.transactionally)
    }
  }
}
