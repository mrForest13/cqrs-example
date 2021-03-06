package com.cqrs.write.http.doc

import akka.http.scaladsl.server.Route
import com.cqrs.common.validation.ValidationErrors
import com.cqrs.write.http.model.BookContent
import io.swagger.annotations._
import javax.ws.rs.Path

@Api(value = "Command api", produces = "application/json")
@Path("/cqrs")
trait BookApiDoc {

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
      new ApiResponse(code = 404, message = "Author or Category does not exist"),
      new ApiResponse(code = 422, message = "Validation desc", response = classOf[ValidationErrors])
    ))
  def addBook: Route

}
