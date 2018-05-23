package connection;

import model.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Test {

    public static List<Product> getProducts() {
        final List<Product> products = new ArrayList<>();
        products.add(new Product(UUID.randomUUID(), "Watch", "description:::1", 800.00, "carson-watch.jpeg"));
        products.add(new Product(UUID.randomUUID(), "Freeze", "description:::Freeze", 8000.00, "carson-watch.jpeg"));
        products.add(new Product(UUID.randomUUID(), "Mixer", "description:::Mixer", 2100.00, "carson-watch.jpeg"));
        products.add(new Product(UUID.randomUUID(), "Cooler", "description:::Cooler", 8000.00, "carson-watch.jpeg"));
        return products;
    }
}
