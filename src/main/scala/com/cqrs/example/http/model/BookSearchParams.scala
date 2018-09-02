package com.cqrs.example.http.model

import com.wix.accord.dsl._
import com.wix.accord.transform.ValidationTransform

final case class BookSearchParams(
  title: Option[String],
  author: Option[String],
  publisher: Option[String],
  category: Option[String])

object BookSearchParams {

  implicit val contentValidator: ValidationTransform.TransformedValidator[BookSearchParams] =
    validator[BookSearchParams] { params =>
      if(params.title.isDefined) {
        params.title.get has between(1, 20)
      }
      if(params.author.isDefined) {
        params.author.get has between(1, 20)
      }
      if(params.publisher.isDefined) {
        params.publisher.get has between(5, 20)
      }
      if(params.category.isDefined) {
        params.category.get has between(3, 15)
      }
      ()
    }
}