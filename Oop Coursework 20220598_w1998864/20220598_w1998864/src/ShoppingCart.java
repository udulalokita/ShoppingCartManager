import java.util.ArrayList;
public class ShoppingCart {

    private int maxQueueCapacity;
    private ArrayList<Product> products;

    public ShoppingCart(int maxQueueCapacity) {
        this.maxQueueCapacity = maxQueueCapacity;
        products = new ArrayList<>();
    }

    public int getMaxQueueCapacity() {
        return maxQueueCapacity;
    }

    public void setMaxQueueCapacity(int maxQueueCapacity) {
        this.maxQueueCapacity = maxQueueCapacity;
    }

    public ArrayList<Product> getProducts() {
        return products;
    }

    public void setProducts(ArrayList<Product> products) {
        this.products = products;
    }

    public void addProduct(Product product) {
        if (products.size() < 50) {
            products.add(product);
        } else {
            System.out.println("Maximum product limit reached!");
        }

    }
    public void removeProduct(String productID) {
        for (Product product : products) {
            if (product.getProductID().equals(productID)) {
                products.remove(product);
                System.out.println("Product deleted: " + product.getProductID() + ". Total products left: " + products.size());
                return;
            }
        }
        System.out.println("Product with ID " + productID + " not found.");
    }

    public double calculateTotalCost(){
        double totalCost = 0;
        for (Product product : products){
            totalCost += product.getPrice();

        }
        return totalCost;
    }
}
