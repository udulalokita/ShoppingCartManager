import java.io.*;
import java.util.*;

public class WestminsterShoppingManager implements ShoppingManager {
    private ArrayList<Product> productArrayList = new ArrayList<>();

    public ArrayList<Product> getProductArrayList() {
        return productArrayList;
    }

    public void setProductArrayList(ArrayList<Product> productArrayList) {
        this.productArrayList = productArrayList;
    }
    public void displayMenu() {
        readInformation();
        System.out.println("arrayList"+ Arrays.toString(productArrayList.toArray()));

        while (true) {
            Scanner input = new Scanner(System.in);
            String option;

            // Display menu options
            System.out.println();
            System.out.println();
            System.out.println("--------- Menu ---------");
            System.out.println();
            System.out.println("01.Add a new product");
            System.out.println("02.Delete a product.");
            System.out.println("03.Print List of Products.");
            System.out.println("04.Save to text file.");
            System.out.println("05.Run GUI.");

            System.out.println();
            System.out.print("Select an Option :");

            option = input.nextLine();
            String optionUpperCase = option.toUpperCase();

            switch (optionUpperCase) {
                case "01", "1":
                    addNewProduct();
                    break;

                case "02", "2":
                    deleteProduct();
                    break;

                case "03", "3":
                    printListOfProducts();
                    break;
                case "04","4":
                    saveFile();
                    break;
                case "05","5":
                    GUI gui = new GUI(this);
            }

        }
    }
    /**
     Add product
     * */
    @Override
    public void addNewProduct() {
        Scanner input = new Scanner(System.in);
        System.out.println("Enter Product Type(Electronics/Clothing): ");
        String productType = input.nextLine();
        Product product = null;
        try {
            if (productType.equalsIgnoreCase("Electronics")) {

                System.out.println("Enter Product ID");
                String productID = input.nextLine();
                System.out.println("Enter Product Name");
                String productName = input.nextLine();
                System.out.println("Enter Number of Available Items");
                int noAvailableItems = input.nextInt();
                System.out.println("Enter Product Price");
                int productPrice = input.nextInt();
                System.out.println("Enter Product Brand");
                String productBrand = input.next();
                System.out.println("Enter Product Warranty Period (Years)");
                int warrantyPeriod = input.nextInt();

                product = new Electronics(productID, productName, noAvailableItems, productPrice, productBrand, warrantyPeriod);


            } else if (productType.equalsIgnoreCase("Clothing")) {
                System.out.println("Enter Product ID");
                String productID = input.nextLine();
                System.out.println("Enter Product Name");
                String productName = input.nextLine();
                System.out.println("Enter Number of Available Items");
                int noAvailableItems = input.nextInt();
                System.out.println("Enter Product Price");
                int productPrice = input.nextInt();
                System.out.println("Enter Product Size");
                int productSize = input.nextInt();
                System.out.println("Enter Product Color");
                String productColor = input.next();

                product = new Clothing(productID, productName, noAvailableItems, productPrice, productSize, productColor);
            } else {
                System.out.println("Invalid Input");
                return;
            }
            if (productArrayList.size() < 50) {
                productArrayList.add(product);
                System.out.println(product.getProductName() + " Added Successfully.");
            } else {
                System.out.println("System has Reached Maximum Capacity.");
            }
        } catch (InputMismatchException exception) {
            System.out.println("Invalid Input");
        }
        System.out.println("After adding products:" +Arrays.toString(productArrayList.toArray()));

    }
    /**
     Delete Product.
     * */
    @Override
    public void deleteProduct() {
        System.out.println(Arrays.toString(productArrayList.toArray()));
        Scanner input = new Scanner(System.in);
        System.out.println("Enter the ProductID that needs to be removed.");
        String productID = input.nextLine();
        Product productToDelete = null;

        for (Product product : productArrayList) {
            if (product.getProductID().equals(productID)) {
                productToDelete = product;
                break;
            }
        }
        if (productToDelete != null) {
            productArrayList.remove(productToDelete);
            System.out.println("Product Removed Successfully.");
            System.out.println("Total Number of Products Remaining: " + productArrayList.size());
        } else {
            System.out.println("Product Not Found.");
        }
        System.out.println("After delete " + Arrays.toString(productArrayList.toArray()));

    }
    /**
     Prints the list of products.
     * */
    @Override
    public void printListOfProducts() {
        if (productArrayList.isEmpty()) {
            System.out.println("The List is Empty.");
        } else {
            Collections.sort(productArrayList, new ProductIdComparator());
            for (Product product : productArrayList) {
                System.out.println("Electronics/Clothing: " + product.getClass().getName());
                System.out.println("Product ID: " + product.getProductID());
                System.out.println("Product Name: " + product.getProductName());
                System.out.println("Number of Available Items: " + product.getNumberOfAvailableItems());
                System.out.println("Product Price: " + product.getPrice());

                if (product.getClass().getName().equalsIgnoreCase("Electronics")) {
                    Electronics electronics = (Electronics) product;
                    System.out.println("Product Brand: " + electronics.getBrand());
                    System.out.println("Product Warranty Period: " + electronics.getWarrantyPeriod());
                } else {
                    Clothing clothing = (Clothing) product;
                    System.out.println("Product Size: " + clothing.getSize());
                    System.out.println("Product Colour: " + clothing.getColour());
                }
                System.out.println();
            }
        }
    }
    /**
     Stores data into a text file
     * */
    @Override
    public void saveFile() {
        try(FileWriter writer = new FileWriter("Products.txt")){
            for (Product product: productArrayList){
                writer.write("Product ID: "+product.getProductID()+","+"Product Name: "+product.getProductName()+","+"Number of available items: "+product.getNumberOfAvailableItems()+","+"Product Price: " +product.getPrice());

                if (product.getClass().getName().equalsIgnoreCase("Electronics")) {
                    Electronics electronics = (Electronics) product;
                    writer.write(","+"Product Brand: "+electronics.getBrand()+","+"Warranty Period: "+electronics.getWarrantyPeriod());
                }else{
                    Clothing clothing = (Clothing) product;
                    writer.write(","+"Product Size: "+clothing.getSize()+","+"Product Color: "+clothing.getColour());
                }
                writer.write(System.getProperty("line.separator"));
            }
            System.out.println("File Saved Successfully.");

        }catch(IOException exception){
            System.out.println("An error occurred. Please try again.");
        }
    }
    /**
     Loads data
     * */
    @Override
    public void readInformation() {
        try (BufferedReader reader = new BufferedReader(new FileReader("Products.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");


                if (parts.length >= 4) {
                    String productID = getValue(parts[0]);
                    String productName = getValue(parts[1]);
                    int availableItems = Integer.parseInt(getValue(parts[2]));
                    double price = Double.parseDouble(getValue(parts[3]));

                    if (line.contains("Product Brand")) {
                        // Reading Electronics Products
                        String brand = getValue(parts[4]);
                        int warrantyPeriod = Integer.parseInt(getValue(parts[5]));
                        productArrayList.add(new Electronics(productID, productName, availableItems, price, brand, warrantyPeriod));
                    } else if (line.contains("Product Size")) {
                        // Reading Clothing Products
                        int size = Integer.parseInt(getValue(parts[4]));
                        String color = getValue(parts[5]);
                        productArrayList.add(new Clothing(productID, productName, availableItems, price, size, color));
                    } else {
                        System.out.println("Invalid line in the file: " + line);
                    }
                } else {
                    System.out.println("Invalid line in the file: " + line);
                }
            }
        } catch (IOException | NumberFormatException exception) {
            System.out.println("Error loading file: " + exception.getMessage());
        }
        printProductList();
    }

    public void printProductList() {
        for (Product product : productArrayList) {
            if (product instanceof Electronics) {
                Electronics electronics = (Electronics) product;
                System.out.println("Electronics: " + electronics.getProductID() + ", " + electronics.getProductName() + ", " + electronics.getBrand() + ", " + electronics.getWarrantyPeriod());
            } else if (product instanceof Clothing) {
                Clothing clothing = (Clothing) product;
                System.out.println("Clothing: " + clothing.getProductID() + ", " + clothing.getProductName() + ", " + clothing.getSize() + ", " + clothing.getColour());
            }
        }
    }



    private String getValue(String part) {
        // Use a regular expression to extract the value after the colon
        String[] keyValue = part.split(":");
        if (keyValue.length == 2) {
            return keyValue[1].trim();
        } else {
            return part.trim();
        }
    }
}
//Comparator Implementation
class ProductIdComparator implements Comparator<Product> {
    @Override
    public int compare(Product p1, Product p2) {
        return p1.getProductID().compareToIgnoreCase(p2.getProductID());
    }
}

/**
 REFERENCES:

 1. GeeksforGeeks. Collections.sort() in Java. Available from https://www.geeksforgeeks.org/collections-sort-java-examples/

 2. oracle. File Handling in Java | Reading and Writing File in Java | Java Training | Edureka. YouTube. Java For Each Loop  https://www.youtube.com/watch?v=SslMi6ptwH8

 3. GeeksforGeeks. Comparator Interface in Java. Available from https://www.geeksforgeeks.org/comparator-interface-java/


 **/