package com.cqrs.write.http.model

import spray.json.DefaultJsonProtocol._
import spray.json.RootJsonFormat
import com.wix.accord.dsl._
import com.wix.accord.transform.ValidationTransform
import io.swagger.annotations.ApiModel

@ApiModel(value = "Author content request")
final case class AuthorContent(firstName: String, lastName: String)

object AuthorContent {
  implicit val formatter: RootJsonFormat[AuthorContent] = jsonFormat2(AuthorContent.apply)

  implicit val contentValidator: ValidationTransform.TransformedValidator[AuthorContent] =
    validator[AuthorContent] { author =>
      author.firstName has between(3, 20)
      author.lastName has between(2, 30)
      ()
    }
}
