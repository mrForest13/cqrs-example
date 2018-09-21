package com.cqrs.read.validation

import com.cqrs.read.ExampleObject
import com.wix.accord.Descriptions.{Generic, Path}
import com.wix.accord.scalatest.ResultMatchers
import org.scalatest.{FlatSpec, Matchers}
import com.wix.accord._

import scala.util.Random

class CategoryValidationTest extends FlatSpec with Matchers with ResultMatchers {

  private val rand = new Random()

  private val category = ExampleObject.categoryContent

  private val incorrectNameLow = rand.nextString(2)
  private val incorrectNameMax = rand.nextString(16)

  "Category content event validator" should "return empty error list" in {
    validate(category) shouldBe Success
  }

  it should "return error message for to short field name key" in {
    validate(category.copy(name = incorrectNameLow)) should failWith(
      RuleViolationMatcher(
        value = incorrectNameLow,
        path = Path(Generic("name")),
        constraint = s"got ${incorrectNameLow.length}, expected between 3 and 15"
      )
    )
  }

  it should "return error message for to long field name key" in {
    validate(category.copy(name = incorrectNameMax)) should failWith(
      RuleViolationMatcher(
        value = incorrectNameMax,
        path = Path(Generic("name")),
        constraint = s"got ${incorrectNameMax.length}, expected between 3 and 15"
      )
    )
  }
}
