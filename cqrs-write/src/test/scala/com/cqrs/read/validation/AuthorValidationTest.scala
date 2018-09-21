package com.cqrs.read.validation

import com.cqrs.read.ExampleObject
import com.wix.accord.Descriptions.{Generic, Path}
import com.wix.accord.scalatest.ResultMatchers
import org.scalatest.{FlatSpec, Matchers}
import com.wix.accord._

import scala.util.Random

class AuthorValidationTest extends FlatSpec with Matchers with ResultMatchers {

  private val rand = new Random()

  private val author = ExampleObject.authorContent

  private val incorrectFirstNameLow = rand.nextString(2)
  private val incorrectFirstNameMax = rand.nextString(21)
  private val incorrectLastNameLow  = rand.nextString(1)
  private val incorrectLastNameMax  = rand.nextString(31)

  "Author content event validator" should "return empty error list" in {
    validate(author) shouldBe Success
  }

  it should "return error message for to short field firstName key" in {
    validate(author.copy(firstName = incorrectFirstNameLow)) should failWith(
      RuleViolationMatcher(
        value = incorrectFirstNameLow,
        path = Path(Generic("firstName")),
        constraint = s"got ${incorrectFirstNameLow.length}, expected between 3 and 20"
      )
    )
  }

  it should "return error message for to long field firstName key" in {
    validate(author.copy(firstName = incorrectFirstNameMax)) should failWith(
      RuleViolationMatcher(
        value = incorrectFirstNameMax,
        path = Path(Generic("firstName")),
        constraint = s"got ${incorrectFirstNameMax.length}, expected between 3 and 20"
      )
    )
  }

  it should "return error message for to short field lastName key" in {
    validate(author.copy(lastName = incorrectLastNameLow)) should failWith(
      RuleViolationMatcher(
        value = incorrectLastNameLow,
        path = Path(Generic("lastName")),
        constraint = s"got ${incorrectLastNameLow.length}, expected between 2 and 30"
      )
    )
  }

  it should "return error message for to long field lastName key" in {
    validate(author.copy(lastName = incorrectLastNameMax)) should failWith(
      RuleViolationMatcher(
        value = incorrectLastNameMax,
        path = Path(Generic("lastName")),
        constraint = s"got ${incorrectLastNameMax.length}, expected between 2 and 30"
      )
    )
  }
}
