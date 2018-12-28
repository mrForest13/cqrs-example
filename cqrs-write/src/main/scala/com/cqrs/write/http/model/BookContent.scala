package com.cqrs.write.http.model
import com.cqrs.write.db.`type`.{Id, Language}
import com.cqrs.write.db.model.Category
import spray.json.RootJsonFormat
import spray.json.DefaultJsonProtocol._
import com.wix.accord.dsl._
import com.wix.accord.transform.ValidationTransform
import io.swagger.annotations.{ApiModel, ApiModelProperty}

import scala.annotation.meta.field

@ApiModel(value = "Book content request")
final case class BookContent(
  title: String,
  publisher: String,
  @(ApiModelProperty @field)(dataType = "String", example = "PL")
  language: Language,
  categoryId: Id[Category],
  description: String)

object BookContent {
  implicit val formatter: RootJsonFormat[BookContent] = jsonFormat5(BookContent.apply)

  implicit val contentValidator: ValidationTransform.TransformedValidator[BookContent] =
    validator[BookContent] { book =>
      book.title has between(3, 20)
      book.publisher has between(3, 20)
      book.description has between(5, 500)
      ()
    }
}
