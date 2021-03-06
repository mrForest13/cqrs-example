package com.cqrs.write.db.dao

import com.cqrs.write.db.BaseDAO
import com.cqrs.write.db.model.Category
import slick.jdbc.JdbcProfile
import slick.lifted.ProvenShape

class CategoryDao(implicit jdbcProfile: JdbcProfile) extends BaseDAO[Category](jdbcProfile) {

  import profile.api._

  type T = CategoryTable

  def tableQuery: TableQuery[CategoryTable] = TableQuery[CategoryTable]

  def findByName(name: String): DBIO[Option[Category]] =
    tableQuery.filter(_.name === name).result.headOption

  final class CategoryTable(tag: Tag) extends BaseTable(tag, "CATEGORY") {

    def name: Rep[String] = column[String]("NAME")

    def * : ProvenShape[Category] =
      (id.?, name, updatedDate.?, createdDate.?) <> (Category.construct, Category.unapply)
  }
}
