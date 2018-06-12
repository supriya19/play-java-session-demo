package controllers;

import com.google.inject.Inject;
import connection.Test;
import model.Product;
import model.ProductForm;
import play.data.Form;
import play.data.FormFactory;
import play.libs.Json;
import play.libs.concurrent.HttpExecutionContext;
import play.libs.ws.WSClient;
import play.libs.ws.WSResponse;
import play.mvc.Controller;
import play.mvc.Result;
import services.ProductService;
import validator.Validator;
import views.html.product.edit;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class ProductController extends Controller {

    private ProductService productService;
    private FormFactory formFactory;
    private HttpExecutionContext executionContext;
    private WSClient wsClient;

    @Inject
    public ProductController(ProductService productService, FormFactory formFactory,
                             HttpExecutionContext executionContext, WSClient wsClient) {
        this.productService = productService;
        this.formFactory = formFactory;
        this.executionContext = executionContext;
        this.wsClient = wsClient;
    }

    public CompletionStage<Result> products() {
        return productService.getProducts().handleAsync((result, error) -> {
            if (error == null) {
                return ok(views.html.product.tables.render(result));
            } else {
                return internalServerError(error.getMessage());
            }
        }).exceptionally(throwable -> badRequest());
    }

    public CompletionStage<Result> createFormView() {
        final Form<ProductForm> productForm = formFactory.form(ProductForm.class);
        return CompletableFuture.completedFuture(ok(views.html.product.create.render(productForm)));
    }

    /**
     * @return
     */
    public CompletionStage<Result> create() {
        final Form<ProductForm> productForm = formFactory.form(ProductForm.class).bindFromRequest();
        if (productForm.hasErrors()) {
            System.out.println(productForm.hasErrors());
            System.out.println("Errors::::" + productForm.allErrors());
            return CompletableFuture.completedFuture(badRequest(views.html.product.create.render(productForm)));
        } else {
            return productService.create(productForm.get()).handleAsync((result, error) -> {
                return redirect(routes.ProductController.products());
            }).exceptionally(throwable -> badRequest(views.html.product.create.render(productForm)));
        }
    }

    /**
     * Example of Custom Action Composition
     *
     * @return
     */
    @Validator(ProductForm.class)
    public CompletionStage<Result> createByJson() {
        final ProductForm productForm = Json.fromJson(request().body().asJson(), ProductForm.class);
        return productService.create(productForm).handleAsync((result, error) -> {
            return redirect(routes.ProductController.products());
        }).exceptionally(throwable -> internalServerError(throwable.getMessage()));
    }

    /**
     * @param productId
     * @return
     */
    public CompletionStage<Result> editFormView(String productId) {
        return productService.getProductById(productId).handleAsync((result, error) -> {
            if (result.isPresent()) {
                Form<Product> productForm = formFactory.form(Product.class).fill(result.get());
                return ok(edit.render("Edit Product", productForm));
            } else {
                return redirect(routes.ProductController.products());
            }
        }, executionContext.current()).exceptionally(throwable -> {
            System.out.println("throwable:::::" + throwable.getMessage());
            return redirect(routes.ProductController.products());
        });
    }

    /**
     * @return
     */
    public CompletionStage<Result> edit() {
        final Form<Product> productForm = formFactory.form(Product.class).bindFromRequest();
        if (productForm.hasErrors()) {
            System.out.println(productForm.hasErrors());
            return CompletableFuture.completedFuture(badRequest(views.html.product.edit.render("Error", productForm)));
        } else {
            return productService.updateProductById(productForm.get()).handleAsync((result, error) -> {
                return redirect(routes.ProductController.products());
            }).exceptionally(throwable -> badRequest(views.html.product.edit.render("Error", productForm)));
        }
    }


    /**
     * To get the Product by given productId
     *
     * @param productId
     * @return
     */
    public CompletionStage<Result> findProductByID(String productId) {
        return productService.getProductById(productId).handleAsync((result, error) -> {
            if (error != null) {
                return internalServerError(error.getMessage());
            } else {
                if (result.isPresent()) {
                    return ok(Json.toJson(result)).as("application/json");
                } else {
                    return notFound("Product not found by given id:::" + productId);
                }
            }
        }, executionContext.current())
                .exceptionally(throwable -> internalServerError(throwable.getMessage()));
    }

    /**
     * To get all the products details
     *
     * @return
     */
    public Result productAll() {
        return ok(Json.toJson(Test.getProducts()));
    }

    /**
     * WS Example with xml response
     *
     * @return
     */
    public CompletionStage<Result> webServiceAsXmlResponse() {
        final CompletionStage<org.w3c.dom.Document> wsDocument = wsClient.url("https://github.com/supriya19")
                .get().thenApply(wsResponse -> {
                    return wsResponse.asXml();
                });

        return wsDocument.thenApply(response -> {
            return ok(response.getTextContent());
        }).exceptionally(throwable -> {
            return internalServerError();
        });
    }

    /**
     * WS example with json response
     *
     * @return
     */
    public CompletionStage<Result> webServiceAsJsonResponse() {
        return wsClient.url("https://github.com/supriya19").get().thenApply(wsResponse -> {
            if (wsResponse.getStatus() == 200) {
                return ok(wsResponse.asJson());
            } else {
                return internalServerError();
            }
        });
    }

    /**
     * Combine WS API calls
     *
     * @return
     */
    public CompletionStage<Result> testCombine() {
        final CompletionStage<WSResponse> githubCompletionStage = wsClient.url("https://github.com/supriya19").get();
        final CompletionStage<WSResponse> googleCompletionStage = wsClient.url("https://www.google.com/").get();

        return githubCompletionStage.thenCombine(googleCompletionStage, (githubRespone, googleResponse) -> {
            return ok(githubRespone.getBody() + googleResponse.getBody());
        });
    }

    /**
     * Combine with exceptionally block
     *
     * @return
     */
    public CompletionStage<Result> testCombineWithRecover() {
        final CompletionStage<WSResponse> githubCompletionStage = wsClient.url("https://github.com/supriya19").get();
        final CompletionStage<WSResponse> googleCompletionStage = wsClient.url("https://www.google.com/").get();

        return githubCompletionStage.thenCombine(googleCompletionStage, (githubRespone, googleResponse) -> {
            return ok(githubRespone.asJson() + googleResponse.getBody());
        }).exceptionally(throwable -> badRequest(throwable.getMessage()));
    }
}
