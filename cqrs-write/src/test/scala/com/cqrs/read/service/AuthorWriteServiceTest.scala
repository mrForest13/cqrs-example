package com.cqrs.read.service

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.testkit.{ImplicitSender, TestKit}
import com.cqrs.read.{DatabaseTest, ExampleObject}
import com.cqrs.write.Core
import com.cqrs.write.db.Id
import com.cqrs.write.db.model.Author
import com.cqrs.write.service.{AuthorService, AuthorServiceComponent}
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach}

import scala.concurrent.ExecutionContext

class AuthorWriteServiceTest
    extends TestKit(ActorSystem("cqrs-system-test"))
    with ImplicitSender
    with Core
    with DatabaseTest
    with AuthorServiceComponent
    with BeforeAndAfterEach
    with BeforeAndAfterAll {

  implicit lazy val executionContext: ExecutionContext = system.dispatcher
  implicit lazy val materializer: ActorMaterializer    = ActorMaterializer()

  val authorService: AuthorService = new AuthorServiceImpl

  override def beforeEach(): Unit = {
    authorDao.initScheme()
  }

  override def afterEach(): Unit = {
    authorDao.dropScheme()
  }

  override def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
    db.close()
  }

  "Author write service" should "return same element after insert new element" in {

    val author = new Author(ExampleObject.authorContent)

    val action = for {
      id   <- authorService.add(author)
      find <- db.run(authorDao.findById(id))
    } yield find

    whenReady(action) { result =>
      result.get shouldBe author.copy(id = Some(Id(1)))
    }
  }
}
