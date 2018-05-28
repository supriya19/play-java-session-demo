package validator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.inject.Inject;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Result;
import scala.xml.dtd.DEFAULT;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

public class ValidatorAction extends Action<Validator> {

    @Inject
    private FormFactory formFactory;

    @Override
    public CompletionStage<Result> call(Http.Context context) {
        final ObjectNode result = play.libs.Json.newObject();
        final  Class clazz = configuration.value();
        if (clazz != DEFAULT.class) {
            final Form<Object> formRequest = formFactory.form(clazz).bind(context.request().body().asJson());
            if (formRequest.hasErrors()) {
                final ObjectMapper mapper = new ObjectMapper();
                final Map<String, List<String>> objectMap = mapper.convertValue(formRequest.errorsAsJson(), Map.class);
                String errorMessage = "invalid json";
                final List<String> values = objectMap.values().stream().
                        flatMap(mapValues -> mapValues.stream()).collect(Collectors.toList());
                if (!values.isEmpty()) {
                    errorMessage = values.stream().toString();
                }
                result.put("status", badRequest().status());
                result.put("message", errorMessage);
                return CompletableFuture.completedFuture(badRequest(result));
            }
        }
        return delegate.call(context);
    }
}
