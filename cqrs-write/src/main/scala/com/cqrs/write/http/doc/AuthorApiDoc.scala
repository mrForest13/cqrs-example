package com.cqrs.write.http.doc

import akka.http.scaladsl.server.Route
import com.cqrs.common.validation.ValidationErrors
import com.cqrs.write.http.model.AuthorContent
import io.swagger.annotations._
import javax.ws.rs.Path

@Api(value = "Command api", produces = "application/json")
@Path("/cqrs")
trait AuthorApiDoc {

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
      new ApiResponse(code = 201, message = "The request has been fulfilled ..."),
      new ApiResponse(code = 422, message = "Validation desc", response = classOf[ValidationErrors])
    ))
  def addAuthor: Route

}
