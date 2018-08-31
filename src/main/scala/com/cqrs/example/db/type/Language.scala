package com.cqrs.example.db.`type`

import java.util.Locale

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
}
