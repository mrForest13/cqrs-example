package com.cqrs.example.db.`type`

import java.util.Locale

import spray.json._

final case class Language(code: String, name: String)

object Language {

  private val isoLanguages: Seq[String] = Locale.getISOLanguages.toList

  private val languages: Map[String, Locale] =
    isoLanguages.map(key => key.toUpperCase -> new Locale(key)).toMap

  def fromCode(code: String): Option[Language] = {
    languages.get(code.toUpperCase).map(locale => Language(code, locale.getDisplayLanguage))
  }

  def fromName(name: String): Option[Language] = {
    languages.find(_._2.getDisplayLanguage.equalsIgnoreCase(name)).map {
      case (code, locale) => Language(code, locale.getDisplayLanguage)
    }
  }

  implicit object LanguageJsonFormat extends RootJsonFormat[Language] {

    override def read(json: JsValue): Language = json match {
      case JsString(code) =>
        fromCode(code).getOrElse {
          throw deserializationError(s"Cannot deserialize value $code to language.")
        }
      case obj => throw deserializationError(s"Cannot deserialize value $obj to language.")
    }

    override def write(obj: Language): JsValue = JsString(obj.code)
  }
}
