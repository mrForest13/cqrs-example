package com.cqrs.example.http.model

import com.cqrs.example.db.Id
import com.cqrs.example.db.`type`.Language
import com.cqrs.example.db.model.Category
import spray.json.RootJsonFormat
import spray.json.DefaultJsonProtocol._
import com.wix.accord.dsl._
import com.wix.accord.transform.ValidationTransform

final case class BookContent(
  title: String,
  publisher: String,
  language: Language,
  categoryId: Id[Category],
  description: String)

object BookContent {
  implicit val formatter: RootJsonFormat[BookContent] = jsonFormat5(BookContent.apply)

  implicit val contentValidator: ValidationTransform.TransformedValidator[BookContent] =
    validator[BookContent] { book =>
      book.title has between(3, 20)
      book.publisher has between(5, 20)
      book.description has between(5, 500)
      ()
    }
}
