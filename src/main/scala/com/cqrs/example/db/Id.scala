package com.cqrs.example.db

import java.sql.Timestamp

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

trait Entity[E] {
  def id: Option[Id[E]]

  def updatedDate: Option[Timestamp]
  def createdDate: Option[Timestamp]
}
