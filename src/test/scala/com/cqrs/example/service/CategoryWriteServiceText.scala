package com.cqrs.example.service

import com.cqrs.example.CqrsTest
import com.cqrs.example.db.Id
import com.cqrs.example.db.model.Category
import com.cqrs.example.utils.ConflictException
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach}

class CategoryWriteServiceText extends CqrsTest with BeforeAndAfterEach with BeforeAndAfterAll {

  override def beforeEach(): Unit = {
    categoryDao.initScheme()
  }

  override def afterEach(): Unit = {
    categoryDao.dropScheme()
  }

  "Category write service" should "return same element after insert new element" in {

    val category = Category(None, "Horror")

    val action = for {
      _    <- categoryWriteService.add(category)
      find <- db.run(categoryDao.findById(Id(1)))
    } yield find

    whenReady(action) { result =>
      result.get shouldBe category.copy(id = Some(Id(1)))
    }
  }

  it should "throw exception after insert existing element" in {

    val category = Category(None, "Horror")

    val action = for {
      _      <- categoryWriteService.add(category)
      result <- categoryWriteService.add(category)
    } yield result

    whenReady(action.failed) { result =>
      result shouldBe a[ConflictException]
    }
  }

}
