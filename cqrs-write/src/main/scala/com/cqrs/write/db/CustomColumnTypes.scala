package com.cqrs.write.db

import com.cqrs.write.db.`type`.Language
import slick.ast.BaseTypedType
import slick.jdbc.JdbcType

trait CustomColumnTypes {
  this: HasJdbcProfile =>

  import profile.api._

  implicit protected def idColumnType[T]: JdbcType[Id[T]] with BaseTypedType[Id[T]] =
    MappedColumnType.base[Id[T], Long](_.value, Id.apply)

  implicit protected val languageType: JdbcType[Language] with BaseTypedType[Language] =
    MappedColumnType.base[Language, String](
      language => language.code,
      string =>
        Language.fromCode(string).getOrElse {
          throw new Exception(s"Cannot deserialize value $string to language object")
      }
    )
}
