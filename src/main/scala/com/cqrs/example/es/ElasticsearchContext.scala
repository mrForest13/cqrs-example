package com.cqrs.example.es

import com.sksamuel.elastic4s.http.ElasticClient

trait ElasticsearchContext {

  val esClient: ElasticClient
}
