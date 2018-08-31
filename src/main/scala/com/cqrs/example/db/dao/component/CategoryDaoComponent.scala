package com.cqrs.example.db.dao.component

import java.sql.Timestamp

import com.cqrs.example.db.dao.BaseDAO
import com.cqrs.example.db.model.Category
import slick.jdbc.JdbcProfile
import slick.lifted.ProvenShape

trait CategoryDaoComponent {

  val categoryDao: CategoryDao

  final class CategoryDao(implicit jdbcProfile: JdbcProfile)
      extends BaseDAO[Category](jdbcProfile) {

    import profile.api._

    type T = CategoryTable

    def tableQuery: TableQuery[CategoryTable] = TableQuery[CategoryTable]

    final class CategoryTable(tag: Tag) extends BaseTable(tag, "CATEGORY") {

      def name: Rep[String]           = column[String]("NAME")
      def updatedDate: Rep[Timestamp] = column[Timestamp]("UPDATED_DATE")
      def createdDate: Rep[Timestamp] = column[Timestamp]("CREATED_DATE")

      def * : ProvenShape[Category] = (id.?, name).mapTo[Category]
    }
  }
}
