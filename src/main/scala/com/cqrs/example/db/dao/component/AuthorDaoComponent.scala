package com.cqrs.example.db.dao.component

import java.sql.Timestamp

import com.cqrs.example.db.dao.BaseDAO
import com.cqrs.example.db.model.Author
import slick.jdbc.JdbcProfile

trait AuthorDaoComponent {

  val authorDao: AuthorDao

  final class AuthorDao(implicit jdbcProfile: JdbcProfile) extends BaseDAO[Author](jdbcProfile) {

    import profile.api._

    type T = AuthorTable

    def tableQuery: TableQuery[AuthorTable] = TableQuery[AuthorTable]

    final class AuthorTable(tag: Tag) extends BaseTable(tag, "AUTHOR") {

      def firstName: Rep[String]      = column[String]("FIRST_NAME")
      def lastName: Rep[String]       = column[String]("LAST_NAME")
      def updatedDate: Rep[Timestamp] = column[Timestamp]("UPDATED_DATE")
      def createdDate: Rep[Timestamp] = column[Timestamp]("CREATED_DATE")

      def * = (id.?, firstName, lastName).mapTo[Author]
    }
  }
}
