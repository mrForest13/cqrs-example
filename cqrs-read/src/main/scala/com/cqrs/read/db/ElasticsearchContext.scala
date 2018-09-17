package com.cqrs.read.db

import com.sksamuel.elastic4s.http.ElasticClient

trait ElasticsearchContext {

  val esClient: ElasticClient
}
