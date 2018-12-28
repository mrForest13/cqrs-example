package com.cqrs.write.db.`type`

import spray.json.{JsNumber, JsValue, JsonFormat, deserializationError}

final case class Id[A](value: Long) extends AnyVal

object Id {

  implicit def jsonFormat[T]: JsonFormat[Id[T]] = new JsonFormat[Id[T]] {
    def read(json: JsValue): Id[T] = json match {
      case JsNumber(value) => Id[T](value.toLong)
      case _               => deserializationError("expecting number with id")
    }

    def write(obj: Id[T]): spray.json.JsValue = JsNumber(obj.value)
  }
}
