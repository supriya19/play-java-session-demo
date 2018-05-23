package controllers;

import play.mvc.Controller;
import play.mvc.Result;

public class ResultsController extends Controller {

    public Result okResult() {
        return ok("success");
    }

    public Result notFoundResult() {
        return notFound("Not found");
    }

    public Result badRequestResult() {
        return badRequest("Bad Request");
    }

    public Result internalServerErrorResult() {
        return internalServerError("Internal Server Error");
    }

    public Result anyStatusResult() {
        return status(500, "Bad Request");
    }

    public Result temporaryRedirect() {
        return temporaryRedirect("/product/list");
    }

}
