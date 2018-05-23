package services;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import connection.SessionReader;
import connection.cassandra.CassandraQuery;
import connection.cassandra.Query;
import model.CommonConstants;
import model.Product;
import model.ProductForm;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class ProductService {

    private SessionReader session;
    private final Map<Query, PreparedStatement> queries;

    @Inject
    public ProductService(@Named(CommonConstants.DATA_READER) SessionReader session,
                          @Named("cassandraQueries") CassandraQuery cassandraQuery) {
        this.session = session;
        this.queries = cassandraQuery == null ? new java.util.EnumMap<>(Query.class) : cassandraQuery.getPreparedStatementMap();

    }

    public CompletionStage<List<Product>> getProducts() {
        List<Product> products = new ArrayList<>();
        if (queries.containsKey(Query.GET_ALL_PRODUCTS)) {
            final PreparedStatement preparedStatement = queries.get(Query.GET_ALL_PRODUCTS);
            final BoundStatement boundStatement = preparedStatement.bind();
            return getRowsFromDatabaseWithStatement(boundStatement).thenApply(productsResponse -> {
                productsResponse.forEach(row -> {
                    Product product = new Product(row.getUUID("id"), row.getString("title"),
                            row.getString("description"), row.getDouble("price"), row.getString("image"));
                    products.add(product);
                });
                return products;
            });
        } else {
            return CompletableFuture.completedFuture(products);
        }
    }

    /**
     * @param productForm
     * @return
     */
    public CompletionStage<String> create(ProductForm productForm) {
        if (queries.containsKey(Query.INSERT_PRODUCTS)) {
            final PreparedStatement preparedStatement = queries.get(Query.INSERT_PRODUCTS);
            final BoundStatement boundStatement = preparedStatement.bind(UUID.randomUUID(), productForm.getTitle(),
                    productForm.getDescription(), productForm.getPrice(), productForm.getImage());
            return getRowsFromDatabaseWithStatement(boundStatement).thenApply(productsResponse -> {
                if (productsResponse.size() > 0) {
                    return "Product created successfully.";
                } else {
                    return "Error while creating product";
                }
            }).exceptionally(throwable -> "Error while creating product:::" + throwable.getMessage());
        } else {
            return CompletableFuture.completedFuture("Not able to find the query.");
        }
    }

    public CompletionStage<String> updateProductById(Product updatedProductData) {
        if (queries.containsKey(Query.UPDATE_PRODUCTS)) {
            final PreparedStatement preparedStatement = queries.get(Query.UPDATE_PRODUCTS);
            final BoundStatement boundStatement = preparedStatement.bind(updatedProductData.getTitle(),
                    updatedProductData.getDescription(), updatedProductData.getPrice(), updatedProductData.getImage(),
                    updatedProductData.getId());
            return getRowsFromDatabaseWithStatement(boundStatement).thenApply(productsResponse -> {
                if (productsResponse.size() > 0) {
                    return "Product created successfully.";
                } else {
                    return "Error while creating product";
                }
            }).exceptionally(throwable -> "Error while creating product:::" + throwable.getMessage());
        } else {
            return CompletableFuture.completedFuture("Not able to find the query.");
        }
    }

    private CompletionStage<List<Row>> getRowsFromDatabaseWithStatement(BoundStatement statement) {
        final CompletionStage<ResultSet> completionStage = CompletableFuture.supplyAsync(() -> session.getDataFromDatabase(statement));
        return completionStage.thenApply(result -> {
            if (result.isExhausted()) {
                return new ArrayList<>();
            }
            return result.all();
        });
    }

    /**
     * @param productId
     * @return
     */
    public CompletionStage<Optional<Product>> getProductById(String productId) {
        List<Product> products = new ArrayList<>();
        try {
            if (queries.containsKey(Query.GET_PRODUCT_BY_ID)) {
                final PreparedStatement preparedStatement = queries.get(Query.GET_PRODUCT_BY_ID);
                final BoundStatement boundStatement = preparedStatement.bind(UUID.fromString(productId));
                return getRowsFromDatabaseWithStatement(boundStatement).thenApply(productsResponse -> {
                    productsResponse.forEach(row -> {
                        Product product = new Product(row.getUUID("id"), row.getString("title"),
                                row.getString("description"), row.getDouble("price"), row.getString("image"));
                        products.add(product);
                    });
                    return products.stream().findFirst();
                }).exceptionally(throwable -> Optional.empty());
            } else {
                return CompletableFuture.completedFuture(products.stream().findFirst());
            }
        } catch (Exception ex) {
            return CompletableFuture.completedFuture(Optional.empty());
        }
    }
}
