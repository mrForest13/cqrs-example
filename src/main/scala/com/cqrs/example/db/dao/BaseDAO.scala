package com.cqrs.example.db.dao

import java.sql.Timestamp

import com.cqrs.example.db.{CustomColumnTypes, Entity, HasJdbcProfile, Id}
import slick.jdbc.JdbcProfile

abstract class BaseDAO[E <: Entity[E]](val profile: JdbcProfile)
    extends HasJdbcProfile
    with CustomColumnTypes {

  import profile.api._

  type T <: BaseTable

  def tableQuery: TableQuery[T]

  def findById(id: Id[E]): DBIO[Option[E]] =
    tableQuery.filter(_.id === id).result.headOption

  def insert(entity: E): DBIO[Id[E]] =
    Queries.returningId += entity

  def update(entity: E): DBIO[Int] =
    Queries.findById(entity.id).update(entity)

  def initTable(): DBIO[Unit] =
    tableQuery.schema.create

  def dropTable(): DBIO[Unit] =
    tableQuery.schema.drop

  abstract class BaseTable(tag: Tag, name: String) extends Table[E](tag, name) {

    def id: Rep[Id[E]] = column[Id[E]]("id", O.PrimaryKey, O.AutoInc)

    def updatedDate: Rep[Timestamp] = column[Timestamp]("UPDATED_DATE")
    def createdDate: Rep[Timestamp] = column[Timestamp]("CREATED_DATE")
  }

  object Queries {

    def findById(id: Id[E]): Query[T, E, Seq] =
      tableQuery filter (_.id === id)

    def findById(id: Option[Id[E]]): Query[T, E, Seq] =
      tableQuery filter (_.id === id)

    def findAll(limit: Int): Query[T, E, Seq] =
      tableQuery take limit

    def returningId: JdbcProfile#ReturningInsertActionComposer[E, Id[E]] =
      tableQuery returning tableQuery.map(_.id)
  }
}
