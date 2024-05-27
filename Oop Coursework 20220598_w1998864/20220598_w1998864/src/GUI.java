import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GUI extends JFrame {

    private JComboBox<String> productTypeComboBox;
    private JTable productTable;
    private JTextArea productDetailsTextArea;
    private JTextArea cartDetailsTextArea;
    private JButton addToCartButton;
    private JButton viewCartButton;
    private ShoppingCart shoppingCart;
    private DefaultTableModel cartTableModel;
    private Object[][] data;
    private ArrayList<Product> productList =new ArrayList<>();
    private Map<String, Integer> productQuantities = new HashMap<>();
    public GUI(WestminsterShoppingManager shoppingManager){

        productList = shoppingManager.getProductArrayList();

        setTitle("Shopping Manager");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Initialize components
        productTypeComboBox = new JComboBox<>(new String[]{"All", "Electronics", "Clothes"});
        productTypeComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateTableGUI();
            }
        });

        productTable = new JTable();
        productDetailsTextArea = new JTextArea(10, 40);
        addToCartButton = new JButton("Add to Shopping Cart");
        viewCartButton = new JButton("View Shopping Cart");

        cartTableModel = new DefaultTableModel(new Object[]{"Product ID", "Quantity", "Price"}, 0);
        cartDetailsTextArea = new JTextArea(10, 40);
        cartDetailsTextArea.setEditable(false);

        // Add components to the frame
        JPanel topPanel = new JPanel();
        topPanel.add(new JLabel("Select Product Type:"));
        topPanel.add(productTypeComboBox);

        JScrollPane tableScrollPane = new JScrollPane(productTable);
        tableScrollPane.setPreferredSize(new Dimension(400, 200));

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BorderLayout());

        bottomPanel.add(new JScrollPane(productDetailsTextArea), BorderLayout.NORTH);
        updateProductDetailsPanel(new Object[]{"", "", "", "", "", ""});

        productTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int selectedRow = productTable.getSelectedRow();
                    if (selectedRow != -1) {
                        Object[] selectedRowData = new Object[productTable.getColumnCount()];
                        for (int i = 0; i < productTable.getColumnCount(); i++) {
                            selectedRowData[i] = productTable.getValueAt(selectedRow, i);
                        }
                        updateProductDetailsPanel(selectedRowData);
                        enableAddToCartButton(true); // Enable the "Add to Shopping Cart" button
                    }
                }
            }
        });

        bottomPanel.add(addToCartButton, BorderLayout.CENTER);
        topPanel.add(viewCartButton, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);
        add(tableScrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        // Action listeners for buttons
        addToCartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addToShoppingCart();
            }
        });

        JPanel cartPanel = new JPanel(new BorderLayout());

        JTable cartTable = new JTable(cartTableModel);

        // JScrollPane for the cartTable
        JScrollPane cartTableScrollPane = new JScrollPane(cartTable);
        cartPanel.add(cartTableScrollPane, BorderLayout.CENTER);

        // a JTextArea for displaying cart details
        JTextArea cartDetailsTextArea = new JTextArea(10, 40);
        cartDetailsTextArea.setEditable(false);  // Set to non-editable
        cartPanel.add(new JScrollPane(cartDetailsTextArea), BorderLayout.SOUTH);

        viewCartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Clearing the cartDetailsTextArea before updating
                cartDetailsTextArea.setText("");


                // Calculate and display total price, firstPurchaseDiscount, threeItemsDiscount, and final price
                double totalPrice = calculateTotalPrice();
                double firstPurchaseDiscount = calculatePurchaseDiscount();
                double threeItemsDiscount = calculateDiscount();
                double finalPrice = totalPrice - threeItemsDiscount - firstPurchaseDiscount;

                cartDetailsTextArea.append("Total Price: $" + totalPrice + "\n");
                cartDetailsTextArea.append("Discount (10): -$" + firstPurchaseDiscount + "\n");
                cartDetailsTextArea.append("Discount (20%): -$" + threeItemsDiscount + "\n");
                cartDetailsTextArea.append("Final Price: $" + finalPrice);

                // Display a message dialog with the cart table and final price
                JOptionPane.showMessageDialog(GUI.this, cartPanel, "Shopping Cart", JOptionPane.PLAIN_MESSAGE);

            }
        });

        pack();
        setVisible(true);

        // Initialize shopping cart
        shoppingCart = new ShoppingCart(2);
    }


    private double calculateTotalPrice() {
        double finalPrice = 0.0;
        int rowCount = cartTableModel.getRowCount();

        for (int i = 0; i < rowCount; i++) {
            double price = (double) cartTableModel.getValueAt(i, 2);
            int quantity = (int) cartTableModel.getValueAt(i, 1);
            finalPrice += price * quantity;
        }

        return finalPrice;
    }

    private void enableAddToCartButton(boolean enable) {
        addToCartButton.setEnabled(enable);
    }
    private void addToShoppingCart() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow != -1) {
            // Get product details from the selected row
            Object[] productDetails = getProductDetailsFromRow(selectedRow);

            String productId = productDetails[0].toString();
            int quantity = 1;
            double price = getPrice(productDetails[0].toString()); // Assuming price is a numeric value

            // Checking if the product already exists in the cart
            int existingRow = findProductRowInCart(productId);

            if (existingRow != -1) {
                // Updating the quantity in the existing row
                int currentQuantity = (int) cartTableModel.getValueAt(existingRow, 1);
                quantity += currentQuantity;
                cartTableModel.setValueAt(quantity, existingRow, 1);
            } else {
                // Add the product to the shopping cart
                Object[] cartItem = {productId, quantity, price};
                cartTableModel.addRow(cartItem);
            }

            productQuantities.put(productId, quantity);

        }
    }

    private int findProductRowInCart(String productId) {
        int rowCount = cartTableModel.getRowCount();
        for (int i = 0; i < rowCount; i++) {
            if (productId.equals(cartTableModel.getValueAt(i, 0).toString())) {
                return i;
            }
        }
        return -1;
    }

    private double getPrice(String productID) {
        for (Product product : productList) {
            if (product.getProductID().equals(productID)) {
                return product.getPrice();
            }
        }
        return 0;
    }


    private Object[] getProductDetailsFromRow(int row) {
        if (row >= 0 && row < productTable.getRowCount()) {
            int columnCount = productTable.getColumnCount();
            Object[] productDetails = new Object[columnCount];

            for (int col = 0; col < columnCount; col++) {
                productDetails[col] = productTable.getValueAt(row, col);
            }

            return productDetails;
        } else {
            return new Object[0];
        }
    }


    private void updateTableGUI() {

        String selectedProductType = (String) productTypeComboBox.getSelectedItem();

        // Filter the productList based on the selectedProductType
        ArrayList<Product> filteredProductList = filterProductList(selectedProductType);

        if (filteredProductList != null) {
            // Initialize the data array with the size 5
            data = new Object[filteredProductList.size()][5];

            int x = 0;

            for (Product product : filteredProductList) {
                if (product instanceof Electronics) {
                    Electronics electronics = (Electronics) product;
                    data[x][0] = product.getProductID();
                    data[x][1] = product.getProductName();
                    data[x][2] = "Electronics";
                    data[x][3] = product.getPrice();
                    data[x][4] = electronics.getBrand() + "," +  electronics.getWarrantyPeriod() ;

                } else if (product instanceof Clothing) {
                    Clothing clothing = (Clothing) product;
                    data[x][0] = product.getProductID();
                    data[x][1] = product.getProductName();
                    data[x][2] = "Clothing";
                    data[x][3] = product.getPrice();
                    data[x][4] = clothing.getSize()+ "," + clothing.getColour();

                }
                x++;
            }

            DefaultTableModel tableModel = new DefaultTableModel(data,
                    new Object[]{"Product ID", "Name", "Category", "Price", "Info"});
            productTable.setModel(tableModel);

            TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
            productTable.setRowSorter(sorter);
            sorter.toggleSortOrder(0); // Sort by product ID initially

            // Update the view
            productTable.repaint();
        }
    }

    private ArrayList<Product> filterProductList(String selectedProductType) {

        ArrayList<Product> filteredList = new ArrayList<>();

        for (Product product : productList) {
            if ("All".equals(selectedProductType) ||
                    ("Electronics".equals(selectedProductType) && product instanceof Electronics) ||
                    ("Clothes".equals(selectedProductType) && product instanceof Clothing)) {
                filteredList.add(product);
            }
        }

        return filteredList;
    }

    private void updateProductDetailsPanel(Object[] productDetails) {

        String productId = productDetails[0].toString();
        String category = productDetails[2].toString();
        String name = productDetails[1].toString();

        int itemsAvailable =  0;

        StringBuilder detailsText = new StringBuilder();
        detailsText.append("Selected Product-Details\n");
        detailsText.append("Product Id: ").append(productId).append("\n");
        detailsText.append("Category: ").append(category).append("\n");
        detailsText.append("Name: ").append(name).append("\n");
        itemsAvailable =  getItemsAvailable(productDetails[0].toString());
        detailsText.append("Items Available: ").append(itemsAvailable).append("\n");


        if (category.equalsIgnoreCase("Electronics")) {
            String brand = getBrand(productDetails[0].toString());
            int warrantyPeriod = getWarranty(productDetails[0].toString());
            detailsText.append("Brand: ").append(brand).append("\n");
            detailsText.append("Warranty Period: ").append(warrantyPeriod).append("\n");
        } else if (category.equalsIgnoreCase("Clothing") ) {
            int size = getClothSize(productDetails[0].toString());
            String color = getColour(productDetails[0].toString());
            detailsText.append("Size: ").append(size).append("\n");
            detailsText.append("Color: ").append(color).append("\n");
        }

        // Update JTextArea
        productDetailsTextArea.setText(detailsText.toString());
    }

    private String getColour(String productID) {
        for (Product product : productList) {
            if (product.getProductID().equals(productID)) {
                return ((Clothing) product).getColour();
            }
        }
        return "";
    }

    private int getClothSize(String productID) {
        for (Product product : productList) {
            if (product.getProductID().equals(productID)) {
                return ((Clothing) product).getSize();
            }
        }
        return 0;
    }

    private String getBrand(String productID) {
        for (Product product : productList) {
            if (product.getProductID().equals(productID)) {
                return ((Electronics) product).getBrand();
            }
        }
        return "";
    }

    private int getWarranty(String productID) {
        for (Product product : productList) {
            if (product.getProductID().equals(productID)) {
                return ((Electronics) product).getWarrantyPeriod();
            }
        }
        return 0;
    }

    private int getItemsAvailable(String productID) {
        for (Product product : productList) {
            if (product.getProductID().equals(productID)) {
                return product.getNumberOfAvailableItems();
            }
        }
        return 0;
    }

    private double calculatePurchaseDiscount() {
        double discount = 0;


        return discount;
    }

    private double calculateDiscount() {
        double discount = 0;


        return discount;
    }


}