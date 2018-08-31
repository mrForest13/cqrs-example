package com.cqrs.example.db.dao

import com.cqrs.example.db.{CustomColumnTypes, HasId, HasJdbcProfile, Id}
import slick.jdbc.JdbcProfile

abstract class BaseDAO[E <: HasId[E]](val profile: JdbcProfile)
    extends HasJdbcProfile
    with CustomColumnTypes {

  import profile.api._

  type T <: BaseTable

  def tableQuery: TableQuery[T]

  def findById(id: Id[E]): DBIO[Option[E]] =
    tableQuery.filter(_.id === id).result.headOption

  def insert(entity: E): DBIO[Int] =
    tableQuery += entity

  def update(entity: E): DBIO[Int] =
    Queries.findById(entity.id).update(entity)

  abstract class BaseTable(tag: Tag, name: String) extends Table[E](tag, name) {

    def id: Rep[Id[E]] = column[Id[E]]("id", O.PrimaryKey, O.AutoInc)
  }

  object Queries {

    def findById(id: Id[E]): Query[T, E, Seq] =
      tableQuery filter (_.id === id)

    def findById(id: Option[Id[E]]): Query[T, E, Seq] =
      tableQuery filter (_.id === id)

    def findAll(limit: Int): Query[T, E, Seq] =
      tableQuery take limit
  }
}
