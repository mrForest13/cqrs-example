package com.cqrs.common

import com.wix.accord.dsl._
import com.wix.accord.transform.ValidationTransform

object PrimitiveSchema {

  case class Street(name: String)
  case class Address(street: Street, city: String, zipcode: Option[String])
  case class Person(name: String, age: Int, address: Address)

  val streetValidator: ValidationTransform.TransformedValidator[Street] =
    validator[Street] { p =>
      p.name is notEmpty
      ()
    }

  val addressValidator: ValidationTransform.TransformedValidator[Address] =
    validator[Address] { a =>
      a.city is notEmpty
      a.street is valid(streetValidator)
      a.zipcode.each is notEmpty
      ()
    }

  val personValidator: ValidationTransform.TransformedValidator[Person] =
    validator[Person] { p =>
      p.name is notEmpty
      p.age should be >= 18
      p.address is valid(addressValidator)
      ()
    }
}
