package com.cqrs.write.config

import pureconfig.loadConfigOrThrow

object AppConfig {
  val config: Config = loadConfigOrThrow[Config]
}

final case class Config(
  app: AppConfig,
  http: HttpConfig,
)

final case class AppConfig(
  actorSystem: String,
  dbConfigFile: String
)

final case class HttpConfig(
  host: String,
  port: Int
)
