package com.malabe.sparedepot.app;

import com.malabe.sparedepot.model.Cart;
import com.malabe.sparedepot.model.CartItem;
import com.malabe.sparedepot.model.Dealer;
import com.malabe.sparedepot.model.Part;
import com.malabe.sparedepot.model.SearchCriteria;
import com.malabe.sparedepot.parser.DirtyLegacyParser;
import com.malabe.sparedepot.persistence.AuditLogger;
import com.malabe.sparedepot.persistence.DealerRepository;
import com.malabe.sparedepot.persistence.InventoryRepository;
import com.malabe.sparedepot.persistence.SettingsRepository;
import com.malabe.sparedepot.service.CheckoutService;
import com.malabe.sparedepot.service.DealerService;
import com.malabe.sparedepot.service.InventoryService;
import com.malabe.sparedepot.util.ValidationUtil;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Separator;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class MainApp extends Application {
    private static final DateTimeFormatter DISPLAY_DATE = DateTimeFormatter.ofPattern("dd-MMM-uuuu");

    private InventoryService inventoryService;
    private DealerService dealerService;
    private CheckoutService checkoutService;
    private Cart cart;

    private final ObservableList<Part> inventoryItems = FXCollections.observableArrayList();
    private final ObservableList<Part> searchItems = FXCollections.observableArrayList();
    private final ObservableList<Dealer> dealerItems = FXCollections.observableArrayList();
    private final ObservableList<CartItem> cartItems = FXCollections.observableArrayList();
    private final ObservableList<String> lowStockItems = FXCollections.observableArrayList();

    private TableView<Part> inventoryTable;
    private TableView<Part> searchTable;
    private TableView<Dealer> dealerTable;
    private TableView<CartItem> cartTable;
    private ListView<String> lowStockList;

    private Label inventoryStatus;
    private Label searchStatus;
    private Label dealerStatus;
    private Label cartStatus;
    private Label summaryLabel;
    private TextArea receiptArea;

    private TextField invCodeField;
    private TextField invNameField;
    private TextField invBrandField;
    private TextField invPriceField;
    private TextField invQtyField;
    private ComboBox<String> invCategoryBox;
    private TextField invDateField;
    private TextField invImageField;
    private TextField thresholdField;

    private TextField searchKeywordField;
    private ComboBox<String> searchCategoryBox;
    private TextField searchMinPriceField;
    private TextField searchMaxPriceField;
    private TextField searchMinQtyField;

    private TextField cartCodeField;
    private TextField cartQtyField;

    @Override
    public void start(Stage stage) {
        inventoryService = new InventoryService(
                new InventoryRepository("inventory_legacy.txt"),
                new SettingsRepository("settings.txt"),
                new AuditLogger("audit_log.txt")
        );
        dealerService = new DealerService(new DealerRepository("dealers_legacy.txt"));
        checkoutService = new CheckoutService(new AuditLogger("audit_log.txt"));
        cart = new Cart();

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(12));
        root.setTop(createHeader());
        root.setCenter(createTabs());

        Scene scene = new Scene(root, 1480, 920);
        stage.setTitle("Malabe Tuk-Tuk & Three-Wheeler Spares Depot");
        stage.setScene(scene);
        stage.show();

        refreshAllViews();
    }

    private VBox createHeader() {
        Label title = new Label("Malabe Tuk-Tuk & Three-Wheeler Spares Depot");
        title.setStyle("-fx-font-size: 26px; -fx-font-weight: bold;");

        summaryLabel = new Label();
        summaryLabel.setStyle("-fx-font-size: 14px;");

        lowStockList = new ListView<String>(lowStockItems);
        lowStockList.setPrefHeight(90);

        VBox header = new VBox(8, title, summaryLabel, new Label("Low-stock warnings"), lowStockList);
        header.setPadding(new Insets(0, 0, 12, 0));
        return header;
    }

    private TabPane createTabs() {
        TabPane tabPane = new TabPane();
        Tab inventoryTab = new Tab("Inventory", createInventoryPane());
        Tab searchTab = new Tab("Search", createSearchPane());
        Tab dealerTab = new Tab("Dealers", createDealerPane());
        Tab cartTab = new Tab("Checkout", createCartPane());
        inventoryTab.setClosable(false);
        searchTab.setClosable(false);
        dealerTab.setClosable(false);
        cartTab.setClosable(false);
        tabPane.getTabs().add(inventoryTab);
        tabPane.getTabs().add(searchTab);
        tabPane.getTabs().add(dealerTab);
        tabPane.getTabs().add(cartTab);
        return tabPane;
    }

    private VBox createInventoryPane() {
        inventoryTable = createPartTable();
        inventoryTable.setItems(inventoryItems);
        inventoryTable.getSelectionModel().selectedItemProperty().addListener(new javafx.beans.value.ChangeListener<Part>() {
            @Override
            public void changed(javafx.beans.value.ObservableValue<? extends Part> observable, Part oldValue, Part newValue) {
                if (newValue != null) {
                    populatePartFields(newValue);
                }
            }
        });

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(8);
        form.add(new Label("Code"), 0, 0);
        form.add(new Label("Name"), 0, 1);
        form.add(new Label("Brand"), 0, 2);
        form.add(new Label("Price"), 0, 3);
        form.add(new Label("Quantity"), 0, 4);
        form.add(new Label("Category"), 0, 5);
        form.add(new Label("Date Added"), 0, 6);
        form.add(new Label("Image File"), 0, 7);
        form.add(new Label("Threshold"), 0, 8);

        invCodeField = new TextField();
        invNameField = new TextField();
        invBrandField = new TextField();
        invPriceField = new TextField();
        invQtyField = new TextField();
        invCategoryBox = new ComboBox<String>();
        invCategoryBox.getItems().addAll("Engine", "Electrical", "Brakes", "Bodywork", "Others");
        invDateField = new TextField();
        invDateField.setPromptText("yyyy-MM-dd or 12/05/2023");
        invImageField = new TextField();
        thresholdField = new TextField();

        form.add(invCodeField, 1, 0);
        form.add(invNameField, 1, 1);
        form.add(invBrandField, 1, 2);
        form.add(invPriceField, 1, 3);
        form.add(invQtyField, 1, 4);
        form.add(invCategoryBox, 1, 5);
        form.add(invDateField, 1, 6);
        form.add(invImageField, 1, 7);
        form.add(thresholdField, 1, 8);

        Button addButton = new Button("Add");
        Button updateButton = new Button("Update");
        Button deleteButton = new Button("Delete");
        Button clearButton = new Button("Clear");
        Button reloadButton = new Button("Reload");
        Button thresholdButton = new Button("Save Threshold");

        addButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String message = inventoryService.addPart(buildPartFromInventoryFields());
                inventoryStatus.setText(message);
                refreshAllViews();
            }
        });

        updateButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String message = inventoryService.updatePart(buildPartFromInventoryFields());
                inventoryStatus.setText(message);
                refreshAllViews();
            }
        });

        deleteButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String message = inventoryService.deletePart(invCodeField.getText());
                inventoryStatus.setText(message);
                refreshAllViews();
            }
        });

        clearButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                clearPartFields();
                inventoryStatus.setText("Fields cleared.");
            }
        });

        reloadButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                inventoryService.load();
                refreshAllViews();
                inventoryStatus.setText("Inventory reloaded.");
            }
        });

        thresholdButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (!ValidationUtil.isNonNegativeInteger(thresholdField.getText())) {
                    inventoryStatus.setText("Threshold must be a non-negative integer.");
                    return;
                }
                inventoryService.setLowStockThreshold(Integer.parseInt(thresholdField.getText().trim()));
                refreshAllViews();
                inventoryStatus.setText("Threshold updated.");
            }
        });

        HBox buttons = new HBox(10, addButton, updateButton, deleteButton, clearButton, reloadButton, thresholdButton);
        buttons.setAlignment(Pos.CENTER_LEFT);

        inventoryStatus = new Label();

        VBox left = new VBox(10, form, buttons, inventoryStatus);
        left.setPrefWidth(420);
        VBox right = new VBox(10, inventoryTable);
        VBox.setVgrow(inventoryTable, Priority.ALWAYS);

        HBox content = new HBox(16, left, right);
        content.setPadding(new Insets(10));
        return new VBox(10, content);
    }

    private VBox createSearchPane() {
        searchTable = createPartTable();
        searchTable.setItems(searchItems);

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(8);
        form.add(new Label("Keyword"), 0, 0);
        form.add(new Label("Category"), 0, 1);
        form.add(new Label("Min Price"), 0, 2);
        form.add(new Label("Max Price"), 0, 3);
        form.add(new Label("Min Quantity"), 0, 4);

        searchKeywordField = new TextField();
        searchCategoryBox = new ComboBox<String>();
        searchCategoryBox.getItems().addAll("", "Engine", "Electrical", "Brakes", "Bodywork", "Others");
        searchMinPriceField = new TextField();
        searchMaxPriceField = new TextField();
        searchMinQtyField = new TextField();

        form.add(searchKeywordField, 1, 0);
        form.add(searchCategoryBox, 1, 1);
        form.add(searchMinPriceField, 1, 2);
        form.add(searchMaxPriceField, 1, 3);
        form.add(searchMinQtyField, 1, 4);

        Button searchButton = new Button("Search");
        Button clearButton = new Button("Clear Filters");
        searchStatus = new Label();

        searchButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                SearchCriteria criteria = new SearchCriteria();
                criteria.setKeyword(searchKeywordField.getText());
                if (searchCategoryBox.getValue() != null && searchCategoryBox.getValue().trim().length() > 0) {
                    criteria.setCategory(searchCategoryBox.getValue());
                }
                if (ValidationUtil.isPositiveNumber(searchMinPriceField.getText())) {
                    criteria.setMinPrice(Double.valueOf(searchMinPriceField.getText().trim()));
                }
                if (ValidationUtil.isPositiveNumber(searchMaxPriceField.getText())) {
                    criteria.setMaxPrice(Double.valueOf(searchMaxPriceField.getText().trim()));
                }
                if (ValidationUtil.isNonNegativeInteger(searchMinQtyField.getText())) {
                    criteria.setMinQuantity(Integer.valueOf(searchMinQtyField.getText().trim()));
                }
                searchItems.setAll(inventoryService.search(criteria));
                searchStatus.setText("Search completed. Results: " + searchItems.size());
            }
        });

        clearButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                searchKeywordField.clear();
                searchCategoryBox.setValue(null);
                searchMinPriceField.clear();
                searchMaxPriceField.clear();
                searchMinQtyField.clear();
                searchItems.clear();
                searchStatus.setText("Filters cleared.");
            }
        });

        HBox buttons = new HBox(10, searchButton, clearButton);
        VBox left = new VBox(10, form, buttons, searchStatus);
        left.setPrefWidth(360);
        VBox right = new VBox(10, searchTable);
        VBox.setVgrow(searchTable, Priority.ALWAYS);

        HBox content = new HBox(16, left, right);
        content.setPadding(new Insets(10));
        return new VBox(10, content);
    }

    private VBox createDealerPane() {
        dealerTable = createDealerTable();
        dealerTable.setItems(dealerItems);

        Button loadAllButton = new Button("Load All Sorted");
        Button randomButton = new Button("Select 4 Random Dealers");
        dealerStatus = new Label();

        loadAllButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                dealerItems.setAll(dealerService.getAllSortedByLocation());
                dealerStatus.setText("Loaded all dealers sorted by location.");
            }
        });

        randomButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                dealerItems.setAll(dealerService.selectFourUniqueDealers());
                dealerStatus.setText("Selected " + dealerItems.size() + " unique dealers.");
            }
        });

        VBox box = new VBox(10, new HBox(10, loadAllButton, randomButton), dealerTable, dealerStatus);
        box.setPadding(new Insets(10));
        VBox.setVgrow(dealerTable, Priority.ALWAYS);
        return box;
    }

    private VBox createCartPane() {
        cartTable = createCartTable();
        cartTable.setItems(cartItems);

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(8);
        form.add(new Label("Part Code"), 0, 0);
        form.add(new Label("Quantity"), 0, 1);

        cartCodeField = new TextField();
        cartQtyField = new TextField();
        form.add(cartCodeField, 1, 0);
        form.add(cartQtyField, 1, 1);

        Button addButton = new Button("Add To Cart");
        Button removeButton = new Button("Remove Selected");
        Button clearButton = new Button("Clear Cart");
        Button checkoutButton = new Button("Checkout");
        cartStatus = new Label();
        receiptArea = new TextArea();
        receiptArea.setEditable(false);
        receiptArea.setPrefRowCount(8);

        addButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String code = cartCodeField.getText();
                if (ValidationUtil.isBlank(code)) {
                    cartStatus.setText("Part code is required.");
                    return;
                }
                if (!ValidationUtil.isNonNegativeInteger(cartQtyField.getText())) {
                    cartStatus.setText("Quantity must be a positive integer.");
                    return;
                }
                int quantity = Integer.parseInt(cartQtyField.getText().trim());
                if (quantity <= 0) {
                    cartStatus.setText("Quantity must be greater than zero.");
                    return;
                }
                Part part = inventoryService.findByCode(code);
                if (part == null) {
                    cartStatus.setText("Part not found.");
                    return;
                }
                if (part.getQuantity() < quantity) {
                    cartStatus.setText("Requested quantity exceeds stock.");
                    return;
                }
                cart.addItem(part, quantity);
                refreshCartView();
                cartStatus.setText("Added to cart.");
            }
        });

        removeButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                CartItem selected = cartTable.getSelectionModel().getSelectedItem();
                if (selected == null) {
                    cartStatus.setText("Select a cart row first.");
                    return;
                }
                cart.getItems().remove(selected);
                refreshCartView();
                cartStatus.setText("Cart row removed.");
            }
        });

        clearButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                cart.clear();
                refreshCartView();
                receiptArea.clear();
                cartStatus.setText("Cart cleared.");
            }
        });

        checkoutButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String message = checkoutService.processCheckout(cart, inventoryService);
                receiptArea.setText(message);
                cartStatus.setText(message);
                refreshAllViews();
            }
        });

        HBox buttons = new HBox(10, addButton, removeButton, clearButton, checkoutButton);
        VBox left = new VBox(10, form, buttons, cartStatus, new Label("Receipt / Checkout Result"), receiptArea);
        left.setPrefWidth(420);
        VBox right = new VBox(10, cartTable);
        VBox.setVgrow(cartTable, Priority.ALWAYS);

        HBox content = new HBox(16, left, right);
        content.setPadding(new Insets(10));
        return new VBox(10, content);
    }

    private TableView<Part> createPartTable() {
        TableView<Part> table = new TableView<Part>();
        table.getColumns().add(createPartColumn("Code", "code", 90));
        table.getColumns().add(createPartColumn("Name", "name", 220));
        table.getColumns().add(createPartColumn("Brand", "brand", 140));
        table.getColumns().add(createPartColumn("Price", "price", 100));
        table.getColumns().add(createPartColumn("Qty", "quantity", 70));
        table.getColumns().add(createPartColumn("Category", "category", 120));
        table.getColumns().add(createPartColumn("Date Added", "dateAdded", 120));
        table.getColumns().add(createPartColumn("Image", "imageFile", 140));
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        return table;
    }

    private TableView<Dealer> createDealerTable() {
        TableView<Dealer> table = new TableView<Dealer>();
        table.getColumns().add(createDealerColumn("Code", "code", 90));
        table.getColumns().add(createDealerColumn("Name", "name", 220));
        table.getColumns().add(createDealerColumn("Phone", "phone", 130));
        table.getColumns().add(createDealerColumn("Location", "location", 150));
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        return table;
    }

    private TableView<CartItem> createCartTable() {
        TableView<CartItem> table = new TableView<CartItem>();
        table.getColumns().add(createCartColumn("Code", "code", 90));
        table.getColumns().add(createCartColumn("Name", "name", 220));
        table.getColumns().add(createCartColumn("Category", "category", 130));
        table.getColumns().add(createCartColumn("Unit Price", "unitPrice", 110));
        table.getColumns().add(createCartColumn("Qty", "quantity", 70));
        table.getColumns().add(createCartColumn("Line Total", "displayLineTotal", 120));
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        return table;
    }

    private TableColumn<Part, Object> createPartColumn(String title, String property, double width) {
        TableColumn<Part, Object> column = new TableColumn<Part, Object>(title);
        column.setCellValueFactory(new PropertyValueFactory<Part, Object>(property));
        column.setPrefWidth(width);
        return column;
    }

    private TableColumn<Dealer, Object> createDealerColumn(String title, String property, double width) {
        TableColumn<Dealer, Object> column = new TableColumn<Dealer, Object>(title);
        column.setCellValueFactory(new PropertyValueFactory<Dealer, Object>(property));
        column.setPrefWidth(width);
        return column;
    }

    private TableColumn<CartItem, Object> createCartColumn(String title, String property, double width) {
        TableColumn<CartItem, Object> column = new TableColumn<CartItem, Object>(title);
        column.setCellValueFactory(new PropertyValueFactory<CartItem, Object>(property));
        column.setPrefWidth(width);
        return column;
    }

    private Part buildPartFromInventoryFields() {
        String code = invCodeField.getText();
        String name = invNameField.getText();
        String brand = invBrandField.getText();
        String priceText = invPriceField.getText();
        String qtyText = invQtyField.getText();
        String category = invCategoryBox.getValue();
        String dateText = invDateField.getText();
        String image = invImageField.getText();

        double price = 0.0;
        if (ValidationUtil.isPositiveNumber(priceText)) {
            price = Double.parseDouble(priceText.trim());
        }
        int quantity = 0;
        if (ValidationUtil.isNonNegativeInteger(qtyText)) {
            quantity = Integer.parseInt(qtyText.trim());
        }
        LocalDate date = parseDate(dateText);
        return new Part(code, name, brand, price, quantity, category, date, image);
    }

    private void populatePartFields(Part part) {
        invCodeField.setText(part.getCode());
        invNameField.setText(part.getName());
        invBrandField.setText(part.getBrand());
        invPriceField.setText(String.valueOf(part.getPrice()));
        invQtyField.setText(String.valueOf(part.getQuantity()));
        invCategoryBox.setValue(part.getCategory());
        if (part.getDateAdded() != null) {
            invDateField.setText(part.getDateAdded().format(DateTimeFormatter.ISO_LOCAL_DATE));
        } else {
            invDateField.clear();
        }
        invImageField.setText(part.getImageFile());
    }

    private void clearPartFields() {
        invCodeField.clear();
        invNameField.clear();
        invBrandField.clear();
        invPriceField.clear();
        invQtyField.clear();
        invCategoryBox.setValue(null);
        invDateField.clear();
        invImageField.clear();
    }

    private void refreshAllViews() {
        inventoryItems.setAll(inventoryService.getAllParts());
        searchItems.setAll(inventoryService.getAllParts());
        lowStockItems.clear();
        List<Part> lowStockParts = inventoryService.getLowStockParts();
        for (int i = 0; i < lowStockParts.size(); i++) {
            Part part = lowStockParts.get(i);
            lowStockItems.add(part.getCode() + " - " + part.getName() + " (Qty: " + part.getQuantity() + ")");
        }
        summaryLabel.setText("Total parts: " + inventoryService.getTotalQuantity() + " | Inventory value: Rs. " + formatMoney(inventoryService.getTotalValue()) + " | Threshold: " + inventoryService.getLowStockThreshold());
        thresholdField.setText(String.valueOf(inventoryService.getLowStockThreshold()));
        dealerItems.setAll(dealerService.getAllSortedByLocation());
        refreshCartView();
    }

    private void refreshCartView() {
        cartItems.setAll(cart.getItems());
    }

    private LocalDate parseDate(String text) {
        if (text == null || text.trim().length() == 0) {
            return null;
        }
        String value = text.trim();
        String[] patterns = new String[] {"uuuu-MM-dd", "dd/MM/uuuu", "MMM d uuuu", "dd-MM-uuuu", "uuuu/MM/dd", "dd-MMM-uuuu"};
        for (int i = 0; i < patterns.length; i++) {
            try {
                return LocalDate.parse(value, DateTimeFormatter.ofPattern(patterns[i]));
            } catch (DateTimeParseException ex) {
                // try next pattern
            }
        }
        return null;
    }

    private String formatMoney(double value) {
        return String.format("%.2f", value);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
