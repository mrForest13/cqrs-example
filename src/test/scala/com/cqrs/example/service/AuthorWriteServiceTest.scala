package com.cqrs.example.service

import com.cqrs.example.CqrsTest
import com.cqrs.example.db.Id
import com.cqrs.example.db.model.Author
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach}

class AuthorWriteServiceTest extends CqrsTest with BeforeAndAfterEach with BeforeAndAfterAll {

  override def beforeEach(): Unit = {
    authorDao.initScheme()
  }

  override def afterEach(): Unit = {
    authorDao.dropScheme()
  }

  "Author write service" should "return same element after insert new element" in {

    val author = Author(None, "Jan", "Nowak")

    val action = for {
      _    <- authorWriteService.add(author)
      find <- db.run(authorDao.findById(Id(1)))
    } yield find

    whenReady(action) { result =>
      result.get shouldBe author.copy(id = Some(Id(1)))
    }
  }
}
