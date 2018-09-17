package com.cqrs.write.service

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.testkit.{ImplicitSender, TestKit}
import com.cqrs.example.{ExampleObject, WriteDbTest}
import com.cqrs.write.db.Id
import com.cqrs.write.db.model.Category
import com.cqrs.write.service.write.CategoryWriteServiceComponent
import com.cqrs.write.utils.ConflictException
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach}

import scala.concurrent.ExecutionContextExecutor

class CategoryWriteServiceText
    extends TestKit(ActorSystem("cqrs-system-test"))
    with ImplicitSender
    with WriteDbTest
    with CategoryServiceComponent
    with BeforeAndAfterEach
    with BeforeAndAfterAll {

  implicit val executionContext: ExecutionContextExecutor = system.dispatcher
  implicit lazy val materializer: ActorMaterializer       = ActorMaterializer()

  val categoryService: CategoryService = new CategoryServiceImpl

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

    val category = new Category(ExampleObject.categoryContent)

    val action = for {
      _    <- categoryService.add(category)
      find <- db.run(categoryDao.findById(Id(1)))
    } yield find

    whenReady(action) { result =>
      result.get shouldBe category.copy(id = Some(Id(1)))
    }
  }

  it should "throw exception after insert existing element" in {

    val category = new Category(ExampleObject.categoryContent)

    val action = for {
      _      <- categoryService.add(category)
      result <- categoryService.add(category)
    } yield result

    whenReady(action.failed) { result =>
      result shouldBe a[ConflictException]
    }
  }
}
