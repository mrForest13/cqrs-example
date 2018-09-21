package com.cqrs.write.http

import com.cqrs.write.http.doc.BookRestApiDoc
import com.github.swagger.akka._
import com.github.swagger.akka.model.Info

class SwaggerDocRestApi(ip: String, port: Int) extends SwaggerHttpService with RestApi {

  override val apiClasses: Set[Class[_]] = Set(classOf[BookRestApiDoc])

  override val host: String = ip + ":" + port
  override val basePath     = "/"
  override val apiDocsPath  = "api-docs"
  override val info =
    Info(version = "1.0",
         title = "Cqrs scala example",
         description = "Cqrs pattern implementation based on mysql, akka and elasticsearch")
}
