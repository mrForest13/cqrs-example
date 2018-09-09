package com.cqrs.example.validation

import com.cqrs.example.ExampleObject
import com.wix.accord.Descriptions.{Generic, Path}
import com.wix.accord.scalatest.ResultMatchers
import org.scalatest.{FlatSpec, Matchers}
import com.wix.accord._

import scala.util.Random

class BookValidationTest extends FlatSpec with Matchers with ResultMatchers {

  private val rand = new Random()

  private val book = ExampleObject.bookContent

  private val incorrectTitleLow       = rand.nextString(2)
  private val incorrectTitleMax       = rand.nextString(21)
  private val incorrectPublisherLow   = rand.nextString(2)
  private val incorrectPublisherMax   = rand.nextString(21)
  private val incorrectDescriptionLow = rand.nextString(4)
  private val incorrectDescriptionMax = rand.nextString(501)

  "Book content event validator" should "return empty error list" in {
    validate(book) shouldBe Success
  }

  it should "return error message for to short field title key" in {
    validate(book.copy(title = incorrectTitleLow)) should failWith(
      RuleViolationMatcher(
        value = incorrectTitleLow,
        path = Path(Generic("title")),
        constraint = s"got ${incorrectTitleLow.length}, expected between 3 and 20"
      )
    )
  }

  it should "return error message for to long field title key" in {
    validate(book.copy(title = incorrectTitleMax)) should failWith(
      RuleViolationMatcher(
        value = incorrectTitleMax,
        path = Path(Generic("title")),
        constraint = s"got ${incorrectTitleMax.length}, expected between 3 and 20"
      )
    )
  }

  it should "return error message for to short field publisher key" in {
    validate(book.copy(publisher = incorrectPublisherLow)) should failWith(
      RuleViolationMatcher(
        value = incorrectPublisherLow,
        path = Path(Generic("publisher")),
        constraint = s"got ${incorrectPublisherLow.length}, expected between 3 and 20"
      )
    )
  }

  it should "return error message for to long field publisher key" in {
    validate(book.copy(publisher = incorrectPublisherMax)) should failWith(
      RuleViolationMatcher(
        value = incorrectPublisherMax,
        path = Path(Generic("publisher")),
        constraint = s"got ${incorrectPublisherMax.length}, expected between 3 and 20"
      )
    )
  }

  it should "return error message for to short field description key" in {
    validate(book.copy(description = incorrectDescriptionLow)) should failWith(
      RuleViolationMatcher(
        value = incorrectDescriptionLow,
        path = Path(Generic("description")),
        constraint = s"got ${incorrectDescriptionLow.length}, expected between 5 and 500"
      )
    )
  }

  it should "return error message for to long field description key" in {
    validate(book.copy(description = incorrectDescriptionMax)) should failWith(
      RuleViolationMatcher(
        value = incorrectDescriptionMax,
        path = Path(Generic("description")),
        constraint = s"got ${incorrectDescriptionMax.length}, expected between 5 and 500"
      )
    )
  }
}
