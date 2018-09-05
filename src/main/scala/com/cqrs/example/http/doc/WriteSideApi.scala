package com.cqrs.example.http.doc

import akka.http.scaladsl.server.Route
import com.cqrs.example.http.model.{AuthorContent, BookContent, CategoryContent}
import io.swagger.annotations._
import javax.ws.rs.Path

@Api(value = "Command api", produces = "application/json")
@Path("/cqrs")
trait WriteSideApi {

  @Path("author")
  @ApiOperation(value = "Adds the author in the system", httpMethod = "POST", code = 201)
  @ApiImplicitParams(
    Array(
      new ApiImplicitParam(name = "body",
        value = "JSON of author personal info",
        required = true,
        dataTypeClass = classOf[AuthorContent],
        paramType = "body")
    ))
  @ApiResponses(
    Array(
      new ApiResponse(code = 500, message = "Internal server error"),
      new ApiResponse(code = 201, message = "The request has been fulfilled ...")
    ))
  def addAuthor: Route

  @Path("category")
  @ApiOperation(value = "Adds the category in the system", httpMethod = "POST", code = 201)
  @ApiImplicitParams(
    Array(
      new ApiImplicitParam(name = "body",
        value = "JSON of category name",
        required = true,
        dataTypeClass = classOf[CategoryContent],
        paramType = "body")
    ))
  @ApiResponses(
    Array(
      new ApiResponse(code = 500, message = "Internal server error"),
      new ApiResponse(code = 201, message = "The request has been fulfilled ..."),
      new ApiResponse(code = 409, message = "Category already exist")
    ))
  def addCategory: Route

  @Path("author/{authorId}/book")
  @ApiOperation(value = "Adds the book in the system", httpMethod = "POST", code = 201)
  @ApiImplicitParams(
    Array(
      new ApiImplicitParam(name = "authorId",
        value = "author id",
        required = true,
        dataType = "Long",
        paramType = "path"),
      new ApiImplicitParam(name = "body",
        value = "JSON of book content",
        required = true,
        dataTypeClass = classOf[BookContent],
        paramType = "body")
    ))
  @ApiResponses(
    Array(
      new ApiResponse(code = 500, message = "Internal server error"),
      new ApiResponse(code = 201, message = "The request has been fulfilled ..."),
      new ApiResponse(code = 404, message = "Author or Category does not exist")
    ))
  def addBook: Route
}
