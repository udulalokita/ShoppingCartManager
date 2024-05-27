public class Clothing extends Product{
    private int size;
    private String colour;

    public Clothing(String productID, String productName, int numberOfAvailableItems, double price, int size, String colour) {
        super(productID, productName, numberOfAvailableItems, price);
        this.size = size;
        this.colour = colour;
    }

    // Getters and setters for Clothing Products
    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getColour() {
        return colour;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }

    @Override
    public String toString() {
        return "Clothing{" +
                "size=" + size +
                ", colour='" + colour + '\'' +
                '}';
    }
}
