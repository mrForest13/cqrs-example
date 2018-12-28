package com.cqrs.write.db.dao

import com.cqrs.write.db.BaseDAO
import com.cqrs.write.db.model.Author
import slick.jdbc.JdbcProfile
import slick.lifted.ProvenShape

class AuthorDao(implicit jdbcProfile: JdbcProfile) extends BaseDAO[Author](jdbcProfile) {

  import profile.api._

  type T = AuthorTable

  def tableQuery: TableQuery[AuthorTable] = TableQuery[AuthorTable]

  final class AuthorTable(tag: Tag) extends BaseTable(tag, "AUTHOR") {

    def firstName: Rep[String] = column[String]("FIRST_NAME")
    def lastName: Rep[String]  = column[String]("LAST_NAME")

    def * : ProvenShape[Author] =
      (id.?, firstName, lastName, updatedDate.?, createdDate.?) <> (Author.construct, Author.unapply)
  }
}
