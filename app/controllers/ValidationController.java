package controllers;

import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;

public class ValidationController extends Controller {

    @BodyParser.Of(BodyParser.Json.class)
    public Result index() {
        Http.RequestBody requestBody = request().body();
        return ok(requestBody.asJson()).as("application/json");
    }
}
