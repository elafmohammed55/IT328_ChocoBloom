
import java.io.Serializable;

public class Product implements Serializable {
    private static final long serialVersionUID = 1L;

    private final int id;
    private final String name;
    private final String description;
    private final double price;
    private final String imagePath;
    private int stock;

    public Product(int id, String name, String description,
                   double price, int stock, String imagePath) {
        if (id < 0 || name == null || name.trim().isEmpty()
                || description == null || !Double.isFinite(price)
                || price < 0 || stock < 0) {
            throw new IllegalArgumentException("Invalid product details");
        }

        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.imagePath = imagePath == null ? "" : imagePath;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public double getPrice() { return price; }
    public String getImagePath() { return imagePath; }

    public synchronized int getStock() { return stock; }

    public synchronized boolean isAvailable() {
        return stock > 0;
    }

    public synchronized boolean reduceStock(int quantity) {
        if (quantity <= 0 || quantity > stock) {
            return false;
        }
        stock -= quantity;
        return true;
    }

    public synchronized void restoreStock(int quantity) {
        if (quantity <= 0 || stock > Integer.MAX_VALUE - quantity) {
            throw new IllegalArgumentException("Invalid restock quantity");
        }
        stock += quantity;
    }

    @Override
    public String toString() {
        return name + " | SAR " + price + " | Stock: " + getStock();
    }
}
