package com.cqrs.common

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.cqrs.common.PrimitiveSchema._
import com.cqrs.common.validation.ValidationDirective
import spray.json._
import org.scalatest.{FlatSpec, Matchers}

class ValidationDirectiveSpec extends FlatSpec with Matchers with ScalatestRouteTest {

  "ValidationDirective" should "respond with StatusCode.OK" in {
    val street  = Street("Pawia")
    val address = Address(street, "Cracow", None)
    val person  = Person("Tomasz", 18, address)

    Get() ~> ValidationDirective.validateReq(person)(personValidator) { complete(StatusCodes.OK) } ~>
      check { status shouldEqual StatusCodes.OK }
  }

  it should "respond with StatusCodes.PreconditionFailed and json containing multi level paths" in {

    val street  = Street("")
    val address = Address(street, "", None)
    val person  = Person("", 18, address)

    val responseJsonString = """{
                                   "desc":{
                                     "name":"name must not be empty",
                                     "address.city":"city must not be empty",
                                     "address.street.name":"name must not be empty"
                                   }
                                 }"""

    Get() ~> ValidationDirective.validateReq(person)(personValidator) { complete(StatusCodes.OK) } ~>
      check {

        val response       = responseAs[String]
        val parsedResponse = response.parseJson

        parsedResponse shouldEqual responseJsonString.parseJson
        status shouldEqual StatusCodes.UnprocessableEntity
      }
  }
}
