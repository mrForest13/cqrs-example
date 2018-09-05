package com.cqrs.example.http.doc

import akka.http.scaladsl.server.Route
import com.cqrs.example.es.BookDocument
import io.swagger.annotations._
import javax.ws.rs.Path

@Api(value = "/cqrs", produces = "application/json")
@Path("/hello")
trait ReadSideDoc {

  @Path("book")
  @ApiOperation(value = "Return Hello greeting",
                httpMethod = "GET",
                response = classOf[BookDocument],
                responseContainer = "List")
  @ApiImplicitParams(
    Array(
      new ApiImplicitParam(name = "title",
                           value = "book title",
                           required = false,
                           dataType = "String",
                           paramType = "query"),
      new ApiImplicitParam(name = "author",
                           value = "book author",
                           required = false,
                           dataType = "String",
                           paramType = "query"),
      new ApiImplicitParam(name = "publisher",
                           value = "book publisher",
                           required = false,
                           dataType = "String",
                           paramType = "query"),
      new ApiImplicitParam(name = "category",
                           value = "book category",
                           required = false,
                           dataType = "String",
                           paramType = "query")
    ))
  @ApiResponses(
    Array(
      new ApiResponse(code = 500, message = "Internal server error")
    ))
  def getBooks: Route
}
