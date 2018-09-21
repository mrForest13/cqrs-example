package com.cqrs.read.config

import com.sksamuel.elastic4s.http.ElasticNodeEndpoint
import pureconfig.loadConfigOrThrow

object AppConfig {
  val config: Config = loadConfigOrThrow[Config]
}

final case class Config(
  app: AppConfig,
  http: HttpConfig,
  elastic: ElasticConfig,
)

final case class AppConfig(
  actorSystem: String
)

final case class HttpConfig(
  host: String,
  port: Int
)

final case class ElasticConfig(
  protocol: String,
  host: String,
  port: Int
) {
  def node: ElasticNodeEndpoint = ElasticNodeEndpoint(protocol, host, port, None)
}