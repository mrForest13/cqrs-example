package com.cqrs.example.http.doc

import akka.http.scaladsl.server.Route
import com.cqrs.example.es.BookDocument
import com.cqrs.example.http.error.ValidationErrors
import io.swagger.annotations._
import javax.ws.rs.Path

@Api(value = "Query api", produces = "application/json")
@Path("/cqrs")
trait ReadSideDoc {

  @Path("book")
  @ApiOperation(value = "Returns books that match the conditions",
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
      new ApiResponse(code = 500, message = "Internal server error"),
      new ApiResponse(code = 422, message = "Validation desc", response = classOf[ValidationErrors])
    ))
  def getBooks: Route
}
