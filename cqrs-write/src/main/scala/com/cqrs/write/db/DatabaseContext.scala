package com.cqrs.write.db

import slick.jdbc.JdbcProfile

trait DatabaseContext {
  this: HasJdbcProfile =>

  import profile.api._

  val db: Database
}

trait HasJdbcProfile {
  val profile: JdbcProfile
}
