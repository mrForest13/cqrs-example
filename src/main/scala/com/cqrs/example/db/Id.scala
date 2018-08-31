package com.cqrs.example.db

final case class Id[A](value: Long) extends AnyVal

trait HasId[E] {
  def id: Option[Id[E]]
}