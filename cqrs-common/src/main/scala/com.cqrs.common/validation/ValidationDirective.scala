package com.cqrs.common.validation

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.{Directive, Directive0, Directives}
import com.wix.accord.Descriptions.Path
import com.wix.accord._

object ValidationDirective extends SprayJsonSupport with Directives {

  def validateReq[T](req: T)(implicit validator: Validator[T]): Directive0 = Directive { inner =>
    validator(req) match {
      case Success => inner(())
      case Failure(violations) =>
        complete((StatusCodes.UnprocessableEntity, ValidationErrors(prepareDesc(violations))))
    }
  }

  private def prepareDesc(violations: Set[Violation]): Map[String, String] = {
    violations
      .flatMap(violation => renderViolation(violation, violation.path))
      .toMap
  }

  private def renderViolation(violation: Violation, path: Path): Map[String, String] = {
    (violation match {
      case groupViolation: GroupViolation =>
        groupViolation.children
          .flatMap(v => renderViolation(v, path ++ v.path))

      case ruleViolation: RuleViolation =>
        Set(Descriptions.render(path) -> ruleViolation.toString)
    }).toMap
  }
}
