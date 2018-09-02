package com.cqrs.example.es

import com.cqrs.example.db.`type`.Language
import com.cqrs.example.db.model.{Author, Book, Category}
import com.sksamuel.elastic4s.analyzers._
import com.sksamuel.elastic4s.http.ElasticDsl
import com.sksamuel.elastic4s.http.ElasticDsl.textField
import com.sksamuel.elastic4s.mappings.MappingDefinition
import spray.json._
import spray.json.DefaultJsonProtocol._

final case class BookDocument(
  title: String,
  author: String,
  publisher: String,
  language: Language,
  category: String,
  description: String) {

  def this(book: Book, author: Author, category: Category) {
    this(
      book.title,
      author.firstName + " " + author.lastName,
      book.publisher,
      book.language,
      category.name,
      book.description
    )
  }
}

object BookDocument {

  val indexName: String   = "book-store"
  val mappingName: String = "book"

  val analyzer: CustomAnalyzerDefinition = CustomAnalyzerDefinition(
    "autocomplete",
    StandardTokenizer,
    LowercaseTokenFilter,
    NGramTokenFilter("autocomplete_filter").minMaxGrams(1, 10)
  )

  val mapping: MappingDefinition = ElasticDsl
    .mapping(mappingName)
    .fields(
      textField("title")
        .analyzer(analyzer.name)
        .searchAnalyzer(StandardAnalyzer),
      textField("author")
        .analyzer(analyzer.name)
        .searchAnalyzer(StandardAnalyzer),
      textField("publisher"),
      textField("language"),
      textField("category"),
      textField("description")
    )

  implicit val formatter: RootJsonFormat[BookDocument] = jsonFormat6(BookDocument.apply)
}
