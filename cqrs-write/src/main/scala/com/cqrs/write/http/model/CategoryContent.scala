package com.cqrs.write.http.model

import spray.json.DefaultJsonProtocol._
import spray.json.RootJsonFormat
import com.wix.accord.dsl._
import com.wix.accord.transform.ValidationTransform
import io.swagger.annotations.ApiModel

@ApiModel(value = "Category content request")
final case class CategoryContent(name: String)

object CategoryContent {
  implicit val formatter: RootJsonFormat[CategoryContent] = jsonFormat1(CategoryContent.apply)

  implicit val contentValidator: ValidationTransform.TransformedValidator[CategoryContent] =
    validator[CategoryContent] { category =>
      category.name has between(3, 15)
      ()
    }
}
