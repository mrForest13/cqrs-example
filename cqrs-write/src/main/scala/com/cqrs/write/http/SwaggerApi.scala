package com.cqrs.write.http

import com.cqrs.write.http.doc.BookApiDoc
import com.github.swagger.akka._
import com.github.swagger.akka.model.Info
import com.google.common.net.HostAndPort

final class SwaggerApi(apiAddress: HostAndPort) extends SwaggerHttpService with Api {

  override val apiClasses: Set[Class[_]] = Set(classOf[AuthorApi], classOf[CategoryApi], classOf[BookApiDoc])

  override val host: String = apiAddress.getHost + ":" + apiAddress.getPort
  override val basePath     = "/"
  override val apiDocsPath  = "api-docs"
  override val info =
    Info(version = "1.0",
         title = "Cqrs scala example",
         description = "Cqrs pattern implementation based on mysql, akka and elasticsearch")
}
