package com.cqrs.write.db.dao

import com.cqrs.write.db.BaseDAO
import com.cqrs.write.db.`type`.{Id, Language}
import com.cqrs.write.db.model.{Author, Book, Category}
import slick.jdbc.JdbcProfile
import slick.lifted.{ForeignKeyQuery, ProvenShape}

class BookDao(val authorDao: AuthorDao, val categoryDao: CategoryDao)(implicit jdbcProfile: JdbcProfile)
    extends BaseDAO[Book](jdbcProfile) {

  import profile.api._

  type T = BookTable

  def tableQuery: TableQuery[BookTable] = TableQuery[BookTable]

  final class BookTable(tag: Tag) extends BaseTable(tag, "BOOK") {

    def title: Rep[String]            = column[String]("TITLE")
    def authorId: Rep[Id[Author]]     = column[Id[Author]]("AUTHOR_ID")
    def publisher: Rep[String]        = column[String]("PUBLISHER")
    def categoryId: Rep[Id[Category]] = column[Id[Category]]("CATEGORY_ID")
    def language: Rep[Language]       = column[Language]("LANGUAGE")
    def description: Rep[String]      = column[String]("DESCRIPTION")

    def authorFk: ForeignKeyQuery[authorDao.AuthorTable, Author] =
      foreignKey("author_fk", authorId, authorDao.tableQuery)(_.id)

    def categoryFk: ForeignKeyQuery[categoryDao.CategoryTable, Category] =
      foreignKey("category_fk", categoryId, categoryDao.tableQuery)(_.id)

    def * : ProvenShape[Book] =
      (id.?, title, authorId, publisher, language, categoryId, description, updatedDate.?, createdDate.?) <> (Book.construct, Book.unapply)
  }
}
