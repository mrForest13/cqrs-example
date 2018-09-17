package com.cqrs.write.service

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.testkit.{ImplicitSender, TestKit}
import com.cqrs.example.{ExampleObject, WriteDbTest}
import com.cqrs.write.db.Id
import com.cqrs.write.db.model.Author
import com.cqrs.write.service.write.AuthorWriteServiceComponent
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach}

import scala.concurrent.ExecutionContextExecutor

class AuthorWriteServiceTest
    extends TestKit(ActorSystem("cqrs-system-test"))
    with ImplicitSender
    with WriteDbTest
    with AuthorServiceComponent
    with BeforeAndAfterEach
    with BeforeAndAfterAll {

  implicit val executionContext: ExecutionContextExecutor = system.dispatcher
  implicit lazy val materializer: ActorMaterializer       = ActorMaterializer()

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
      _    <- authorService.add(author)
      find <- db.run(authorDao.findById(Id(1)))
    } yield find

    whenReady(action) { result =>
      result.get shouldBe author.copy(id = Some(Id(1)))
    }
  }
}
