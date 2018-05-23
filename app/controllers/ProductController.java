package controllers;

import com.google.inject.Inject;
import connection.Test;
import model.Product;
import model.ProductForm;
import play.data.Form;
import play.data.FormFactory;
import play.libs.Json;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Controller;
import play.mvc.Result;
import services.ProductService;
import views.html.product.edit;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class ProductController extends Controller {

    @Inject
    private ProductService productService;
    @Inject
    private FormFactory formFactory;
    @Inject
    HttpExecutionContext executionContext;

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
     * @param productId
     * @return
     */
    public CompletionStage<Result> editFormView(String productId) {
        return productService.getProductById(productId).handleAsync((result, error) -> {
            System.out.println("result:::::" + result);
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
}
