
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Category implements Serializable {
    private static final long serialVersionUID = 1L;

    private final int id;
    private final String name;
    private final List<Product> products = new ArrayList<>();

    public Category(int id, String name) {
        if (id < 0 || name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid category");
        }
        this.id = id;
        this.name = name;
    }

    public int getId() { return id; }
    public String getName() { return name; }

    public void addProduct(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Product is null");
        }

        for (Product existing : products) {
            if (existing.getId() == product.getId()) {
                throw new IllegalArgumentException("Duplicate product id");
            }
        }
        products.add(product);
    }

    public List<Product> getProducts() {
        return Collections.unmodifiableList(
            new ArrayList<>(products)
        );
    }
}
