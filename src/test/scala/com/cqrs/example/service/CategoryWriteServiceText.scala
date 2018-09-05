package com.cqrs.example.service

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.testkit.{ImplicitSender, TestKit}
import com.cqrs.example.WriteDbTest
import com.cqrs.example.db.Id
import com.cqrs.example.db.model.Category
import com.cqrs.example.service.write.{CategoryWriteService, CategoryWriteServiceComponent}
import com.cqrs.example.utils.ConflictException
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach}

import scala.concurrent.ExecutionContextExecutor

class CategoryWriteServiceText
    extends TestKit(ActorSystem("cqrs-system-test"))
    with ImplicitSender
    with WriteDbTest
    with CategoryWriteServiceComponent
    with BeforeAndAfterEach
    with BeforeAndAfterAll {

  implicit val executionContext: ExecutionContextExecutor = system.dispatcher
  implicit lazy val materializer: ActorMaterializer       = ActorMaterializer()

  val categoryWriteService: CategoryWriteService = new CategoryWriteServiceImpl

  override def beforeEach(): Unit = {
    categoryDao.initScheme()
  }

  override def afterEach(): Unit = {
    categoryDao.dropScheme()
  }

  override def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
    db.close()
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
