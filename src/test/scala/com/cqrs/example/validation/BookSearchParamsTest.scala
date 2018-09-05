package com.cqrs.example.validation

import com.cqrs.example.http.model.BookSearchParams
import com.wix.accord.Descriptions.{Generic, Path}
import com.wix.accord.scalatest.ResultMatchers
import com.wix.accord.{Success, validate}
import org.scalatest.{FlatSpec, Matchers}

import scala.util.Random

class BookSearchParamsTest extends FlatSpec with Matchers with ResultMatchers {

  private val rand = new Random()

  private val params = new BookSearchParams(None, None, None, None)

  private val incorrectTitleLow     = Some(rand.nextString(0))
  private val incorrectTitleMax     = Some(rand.nextString(21))
  private val incorrectAuthorLow    = Some(rand.nextString(0))
  private val incorrectAuthorMax    = Some(rand.nextString(21))
  private val incorrectPublisherLow = Some(rand.nextString(2))
  private val incorrectPublisherMax = Some(rand.nextString(21))
  private val incorrectCategoryLow  = Some(rand.nextString(2))
  private val incorrectCategoryMax  = Some(rand.nextString(16))

  "Book search params content event validator" should "return empty error list" in {
    validate(params) shouldBe Success
  }

  it should "return error message for to short field title key" in {
    validate(params.copy(title = incorrectTitleLow)) should failWith(
      RuleViolationMatcher(
        value = incorrectTitleLow.get.length,
        path = Path(Generic("title")),
        constraint = s"got ${incorrectTitleLow.get.length}, expected between 1 and 20"
      )
    )
  }

  it should "return error message for to long field title key" in {
    validate(params.copy(title = incorrectTitleMax)) should failWith(
      RuleViolationMatcher(
        value = incorrectTitleMax.get.length,
        path = Path(Generic("title")),
        constraint = s"got ${incorrectTitleMax.get.length}, expected between 1 and 20"
      )
    )
  }

  it should "return error message for to short field author key" in {
    validate(params.copy(author = incorrectAuthorLow)) should failWith(
      RuleViolationMatcher(
        value = incorrectAuthorLow.get.length,
        path = Path(Generic("author")),
        constraint = s"got ${incorrectAuthorLow.get.length}, expected between 1 and 20"
      )
    )
  }

  it should "return error message for to long field author key" in {
    validate(params.copy(author = incorrectAuthorMax)) should failWith(
      RuleViolationMatcher(
        value = incorrectAuthorMax.get.length,
        path = Path(Generic("author")),
        constraint = s"got ${incorrectAuthorMax.get.length}, expected between 1 and 20"
      )
    )
  }

  it should "return error message for to short field publisher key" in {
    validate(params.copy(publisher = incorrectPublisherLow)) should failWith(
      RuleViolationMatcher(
        value = incorrectPublisherLow.get.length,
        path = Path(Generic("publisher")),
        constraint = s"got ${incorrectPublisherLow.get.length}, expected between 3 and 20"
      )
    )
  }

  it should "return error message for to long field publisher key" in {
    validate(params.copy(publisher = incorrectPublisherMax)) should failWith(
      RuleViolationMatcher(
        value = incorrectPublisherMax.get.length,
        path = Path(Generic("publisher")),
        constraint = s"got ${incorrectPublisherMax.get.length}, expected between 3 and 20"
      )
    )
  }

  it should "return error message for to short field category key" in {
    validate(params.copy(category = incorrectCategoryLow)) should failWith(
      RuleViolationMatcher(
        value = incorrectCategoryLow.get.length,
        path = Path(Generic("category")),
        constraint = s"got ${incorrectCategoryLow.get.length}, expected between 3 and 15"
      )
    )
  }

  it should "return error message for to long field category key" in {
    validate(params.copy(category = incorrectCategoryMax)) should failWith(
      RuleViolationMatcher(
        value = incorrectCategoryMax.get.length,
        path = Path(Generic("category")),
        constraint = s"got ${incorrectCategoryMax.get.length}, expected between 3 and 15"
      )
    )
  }
}
