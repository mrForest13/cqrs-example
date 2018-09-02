package com.cqrs.example.service.write

import com.cqrs.example.db.model.Category
import com.cqrs.example.utils.ConflictException
import com.cqrs.example.{Core, WriteDatabaseLayer}

import scala.concurrent.Future

trait CategoryWriteService {
  def add(category: Category): Future[Unit]
}

trait CategoryWriteServiceComponent {

  this: WriteDatabaseLayer with Core =>

  val categoryWriteService: CategoryWriteService

  class CategoryWriteServiceImpl extends CategoryWriteService {

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
