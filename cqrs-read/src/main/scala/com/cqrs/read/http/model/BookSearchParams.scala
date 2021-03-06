package com.cqrs.read.http.model

import com.wix.accord.{Success, Validator}
import com.wix.accord.dsl._
import com.wix.accord.transform.ValidationTransform

final case class BookSearchParams(
  title: Option[String] = None,
  author: Option[String] = None,
  publisher: Option[String] = None,
  category: Option[String] = None)

object BookSearchParams {

  private def validOption[T](min: Int, max: Int): Validator[Option[T]] = {
    case Some(v) => between(min, max).apply(v.toString.length)
    case None    => Success
  }

  implicit val contentValidator: ValidationTransform.TransformedValidator[BookSearchParams] =
    validator[BookSearchParams] { params =>
      params.title must validOption(1, 20)
      params.author must validOption(1, 20)
      params.publisher must validOption(3, 20)
      params.category must validOption(3, 15)
      ()
    }
}
