package com.cqrs.example.db

import spray.json.RootJsonFormat
import spray.json.DefaultJsonProtocol._

final case class Id[A](value: Long) extends AnyVal

object Id {
  implicit def idFormat[A]: RootJsonFormat[Id[A]] = jsonFormat1(Id.apply[A])
}

trait HasId[E] {
  def id: Option[Id[E]]
}
