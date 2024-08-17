import java.time.LocalDate;
import java.util.Optional;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class CakeOrderManager2 extends Application {
	
	private DatabaseManager db;
	private Button takeOrderButton;
	private Button markOrderAsPaidButton;
	private Button checkDueDatesButton;
	private Button completeOrderButton;
	private Button cakeTypeButton;
	private Button quitButton;

    @Override
    public void start(Stage primaryStage) throws ClassNotFoundException {
    	db = new DatabaseManager();
    	
        // Create the grid pane
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(8);
        grid.setHgap(10);

        // Create the buttons
        takeOrderButton = new Button("Take Order");
        markOrderAsPaidButton = new Button("Mark Order as Paid");
        checkDueDatesButton = new Button("Check Due Dates");
        completeOrderButton = new Button("Complete Order");
        cakeTypeButton = new Button("Cake Type Changes");
        quitButton = new Button("Quit");
        
        // Disable all buttons when one of them is clicked
        takeOrderButton.setOnAction(e -> {
            disableButtons();
            takeOrderButtonClicked();
        });
        
        markOrderAsPaidButton.setOnAction(e -> {
            disableButtons();
            markOrderAsPaidButtonClicked();
        });

        checkDueDatesButton.setOnAction(e -> {
            disableButtons();
            checkDueDatesButtonClicked();
        });

        completeOrderButton.setOnAction(e -> {
            disableButtons();
            completeOrderButtonClicked();
        });

        cakeTypeButton.setOnAction(e -> {
            disableButtons();
            cakeTypeButtonClicked();
        });

        quitButton.setOnAction(e -> {
            disableButtons();
            quitButtonClicked();
        });

        // Add the buttons to the grid pane
        grid.add(takeOrderButton, 0, 0);
        grid.add(markOrderAsPaidButton, 1, 0);
        grid.add(checkDueDatesButton, 0, 1);
        grid.add(completeOrderButton, 1, 1);
        grid.add(cakeTypeButton, 0, 2);
        grid.add(quitButton, 1, 2);

        // Set up the scene and show the stage
        Scene scene = new Scene(grid, 300, 200);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
 // Method to disable all buttons
    private void disableButtons() {
        takeOrderButton.setDisable(true);
        markOrderAsPaidButton.setDisable(true);
        checkDueDatesButton.setDisable(true);
        completeOrderButton.setDisable(true);
        cakeTypeButton.setDisable(true);
        quitButton.setDisable(true);
    }

    // Method to enable all buttons
    private void enableButtons() {
        takeOrderButton.setDisable(false);
        markOrderAsPaidButton.setDisable(false);
        checkDueDatesButton.setDisable(false);
        completeOrderButton.setDisable(false);
        cakeTypeButton.setDisable(false);
        quitButton.setDisable(false);
    }
    
    private void takeOrderButtonClicked() {
        Stage orderStage = createOrderStage();
        orderStage.show();
    }

    private Stage createOrderStage() {
        Stage orderStage = new Stage();
        orderStage.setTitle("Take Order");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(8);
        grid.setHgap(10);

        Label nameLabel = new Label("Name:");
        TextField nameField = new TextField();

        Label dueDateLabel = new Label("Due Date:");
        DatePicker dueDatePicker = new DatePicker();

        Label cakeTypeLabel = new Label("Cake Type:");
        ComboBox cakeTypeComboBox = new ComboBox<>();

        db.getCakeTypes(cakeTypeComboBox);

        grid.add(nameLabel, 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(dueDateLabel, 0, 1);
        grid.add(dueDatePicker, 1, 1);
        grid.add(cakeTypeLabel, 0, 2);
        grid.add(cakeTypeComboBox, 1, 2);

        Button submitButton = new Button("Submit");
        submitButton.setOnAction(e -> {
            if (validateOrderData(nameField.getText(), dueDatePicker.getValue(), cakeTypeComboBox.getSelectionModel().getSelectedItem())) {
                db.takeOrder(nameField.getText(), dueDatePicker.getValue(), (String) cakeTypeComboBox.getSelectionModel().getSelectedItem());
                orderStage.close();
                enableButtons();
            }
        });

        grid.add(submitButton, 1, 3);

        Scene scene = new Scene(grid, 300, 200);
        orderStage.setScene(scene);

        return orderStage;
    }

    private boolean validateOrderData(String name, LocalDate dueDate, Object cakeType) {
        if (name.isEmpty() || dueDate == null || cakeType == null) {
            Alert alert = AlertUtil.createAlert(AlertType.ERROR, "Invalid Input", "Please fill in all fields");
            alert.showAndWait();
            return false;
        } else if (dueDate.isBefore(LocalDate.now())) {
            Alert alert = AlertUtil.createAlert(AlertType.ERROR, "Invalid Date", "Due date must be in the future");
            alert.showAndWait();
            return false;
        }
        return true;
    }

    private void markOrderAsPaidButtonClicked() {
    	// Create a new stage for the order id form
        Stage orderIdStage = new Stage();
        orderIdStage.setTitle("Mark Order as Paid");
        
        // Create a grid pane to hold the form fields
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(8);
        grid.setHgap(10);
        
        // Create form fields
        Label orderIdLabel = new Label("Order ID:");
        TextField orderIdField = new TextField();
        
        // Add form fields to the grid pane
        grid.add(orderIdLabel, 0, 0);
        grid.add(orderIdField, 1, 0);
        
        // Create a submit button
        Button submitButton = new Button("Submit");
        submitButton.setOnAction(e -> {
            // Get the order id from the text field
            int orderId;
            try {
                orderId = Integer.parseInt(orderIdField.getText());
            } catch (NumberFormatException ex) {
                Alert alert = AlertUtil.createAlert(AlertType.ERROR, "Invalid Order ID", "Please enter a valid order ID");
                alert.showAndWait();
                return;
            }
            
            // Mark the order as paid in the database
            db.markOrderPaid(orderId);
            enableButtons();
            orderIdStage.close();
        });
        
        // Add the submit button to the grid pane
        grid.add(submitButton, 1, 1);
        
        // Set up the scene and show the stage
        Scene scene = new Scene(grid, 250, 100);
        orderIdStage.setScene(scene);
        orderIdStage.show();
    }

    private void checkDueDatesButtonClicked() {
    	db.checkDates();
    	enableButtons();
    }

    private void completeOrderButtonClicked() {
    	// Create a new stage for the order id form
        Stage orderIdStage = new Stage();
        orderIdStage.setTitle("Complete Order");

        // Create a grid pane to hold the form fields
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(8);
        grid.setHgap(10);

        // Create form fields
        Label orderIdLabel = new Label("Order ID:");
        TextField orderIdField = new TextField();

        // Add form fields to the grid pane
        grid.add(orderIdLabel, 0, 0);
        grid.add(orderIdField, 1, 0);

        // Create a submit button
        Button submitButton = new Button("Submit");
        submitButton.setOnAction(e -> {
            // Get the order id from the text field
            int orderId;
            try {
                orderId = Integer.parseInt(orderIdField.getText());
            } catch (NumberFormatException ex) {
            	Alert alert = AlertUtil.createAlert(AlertType.ERROR, "Invalid Order ID", "Please enter a valid order ID");
                alert.showAndWait();
                return;
            }

            // Complete the order in the database
            db.completeOrder(orderId);
            enableButtons();
            orderIdStage.close();
        });

        // Add the submit button to the grid pane
        grid.add(submitButton, 1, 1);

        // Set up the scene and show the stage
        Scene scene = new Scene(grid, 250, 100);
        orderIdStage.setScene(scene);
        orderIdStage.show();
    }
    
    private void cakeTypeButtonClicked() {
        Stage passwordStage = createPasswordStage();
        passwordStage.show();
    }

    private Stage createPasswordStage() {
        Stage passwordStage = new Stage();
        passwordStage.setTitle("Enter Password");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(8);
        grid.setHgap(10);

        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();

        grid.add(passwordLabel, 0, 0);
        grid.add(passwordField, 1, 0);

        Button submitButton = new Button("Submit");
        submitButton.setOnAction(e -> {
            if (validatePassword(passwordField.getText())) {
                passwordStage.close();
                showCakeAdjustmentsPage();
            }
        });

        grid.add(submitButton, 1, 1);

        Scene scene = new Scene(grid, 250, 100);
        passwordStage.setScene(scene);

        return passwordStage;
    }

    private boolean validatePassword(String password) {
        if (!password.equals("adminpassword")) {
            Alert alert = AlertUtil.createAlert(AlertType.ERROR, "Invalid Password", "Please enter a valid password");
            alert.showAndWait();
            return false;
        }
        return true;
    }

    private void showCakeAdjustmentsPage() {
        Stage cakeAdjustmentsStage = new Stage();
        cakeAdjustmentsStage.setTitle("Cake Changes");

        GridPane cakeTypeGrid = new GridPane();
        cakeTypeGrid.setPadding(new Insets(10, 10, 10, 10));
        cakeTypeGrid.setVgap(8);
        cakeTypeGrid.setHgap(10);

        Button addCakeTypeButton = new Button("Add Cake Type");
        addCakeTypeButton.setOnAction(e -> {
            addNewCakeType();
        });

        Button updateCakeTypeButton = new Button("Update Cake Type");
        updateCakeTypeButton.setOnAction(e -> {
            updateCakeType();
        });

        Button removeCakeTypeButton = new Button("Remove Cake Type");
        removeCakeTypeButton.setOnAction(e -> {
            removeCakeType();
        });

        cakeTypeGrid.add(addCakeTypeButton, 0, 0);
        cakeTypeGrid.add(updateCakeTypeButton, 0, 1);
        cakeTypeGrid.add(removeCakeTypeButton, 0, 2);
        cakeAdjustmentsStage.setOnCloseRequest(ex -> {
	          enableButtons();
	      });
        Scene cakeTypeScene = new Scene(cakeTypeGrid, 200, 150);
        cakeAdjustmentsStage.setScene(cakeTypeScene);
        cakeAdjustmentsStage.show();
    }
    
    private void addNewCakeType() {
        Stage cakeTypeStage = createCakeTypeStage();
        cakeTypeStage.show();
    }

    private Stage createCakeTypeStage() {
        Stage cakeTypeStage = new Stage();
        cakeTypeStage.setTitle("Add New Cake Type");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(8);
        grid.setHgap(10);

        Label nameLabel = new Label("Name:");
        TextField nameField = new TextField();

        Label priceLabel = new Label("Price:");
        TextField priceField = new TextField();

        Label writingLabel = new Label("Writing:");
        TextField writingField = new TextField();

        grid.add(nameLabel, 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(priceLabel, 0, 1);
        grid.add(priceField, 1, 1);
        grid.add(writingLabel, 0, 2);
        grid.add(writingField, 1, 2);

        Button submitButton = new Button("Submit");
        submitButton.setOnAction(e -> {
            if (validateCakeTypeData(nameField.getText(), priceField.getText())) {
                db.addCake(nameField.getText(), Double.parseDouble(priceField.getText()), writingField.getText());
                cakeTypeStage.close();
            }
        });

        grid.add(submitButton, 1, 3);

        Scene scene = new Scene(grid, 300, 200);
        cakeTypeStage.setScene(scene);

        return cakeTypeStage;
    }

    private boolean validateCakeTypeData(String name, String price) {
        if (name.isEmpty() || price.isEmpty()) {
            Alert alert = AlertUtil.createAlert(AlertType.ERROR, "Invalid Input", "Please fill in all fields");
            alert.showAndWait();
            return false;
        }
        try {
            Double.parseDouble(price);
        } catch (NumberFormatException ex) {
            Alert alert = AlertUtil.createAlert(AlertType.ERROR, "Invalid Price", "Please enter a valid price");
            alert.showAndWait();
            return false;
        }
        return true;
    }
    
    private void updateCakeType() {
        Stage updateCakeTypeStage = createUpdateCakeTypeStage();
        updateCakeTypeStage.show();
    }

    private Stage createUpdateCakeTypeStage() {
        Stage updateCakeTypeStage = new Stage();
        updateCakeTypeStage.setTitle("Update Cake Type");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(8);
        grid.setHgap(10);

        ComboBox<String> cakeTypeComboBox = new ComboBox<>();
        db.getCakeTypes(cakeTypeComboBox);

        Label priceLabel = new Label("Price:");
        TextField priceField = new TextField();
        priceField.setDisable(true); // Disable until a cake type is selected

        Label writingLabel = new Label("Writing:");
        TextField writingField = new TextField();
        writingField.setDisable(true); // Disable until a cake type is selected

        grid.add(new Label("Cake Type:"), 0, 0);
        grid.add(cakeTypeComboBox, 1, 0);
        grid.add(priceLabel, 0, 1);
        grid.add(priceField, 1, 1);
        grid.add(writingLabel, 0, 2);
        grid.add(writingField, 1, 2);

        Button submitButton = new Button("Submit");
        submitButton.setDisable(true); // Disable until a cake type is selected
        grid.add(submitButton, 1, 3);

        cakeTypeComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            priceField.setDisable(false);
            writingField.setDisable(false);
            submitButton.setDisable(false);
        });

        submitButton.setOnAction(e -> {
            // Get the selected cake type and form field values
            String cakeType = cakeTypeComboBox.getSelectionModel().getSelectedItem();
            double price = Double.parseDouble(priceField.getText());;
            String writing = writingField.getText();

            // Update the cake type in the database
            db.updateCake(price, writing, cakeType);
            updateCakeTypeStage.close();
        });

        Scene scene = new Scene(grid, 300, 200);
        updateCakeTypeStage.setScene(scene);

        return updateCakeTypeStage;
    }

    private void removeCakeType() {
        Stage removeCakeTypeStage = createRemoveCakeTypeStage();
        removeCakeTypeStage.show();
    }

    private Stage createRemoveCakeTypeStage() {
        Stage removeCakeTypeStage = new Stage();
        removeCakeTypeStage.setTitle("Remove Cake Type");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(8);
        grid.setHgap(10);

        ComboBox<String> cakeTypeComboBox = new ComboBox<>();
        db.getCakeTypes(cakeTypeComboBox);

        grid.add(new Label("Cake Type:"), 0, 0);
        grid.add(cakeTypeComboBox, 1, 0);

        Button submitButton = new Button("Submit");
        submitButton.setDisable(true); // Disable until a cake type is selected
        grid.add(submitButton, 1, 1);

        cakeTypeComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            submitButton.setDisable(false);
        });

        submitButton.setOnAction(e -> {
            String cakeType = cakeTypeComboBox.getSelectionModel().getSelectedItem();

            Alert confirmAlert = AlertUtil.createAlert(AlertType.CONFIRMATION, "Confirm Removal", "Are you sure you want to remove the cake type '" + cakeType + "'?");
            Optional<ButtonType> result = confirmAlert.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK) {
                db.removeCake(cakeType);
                removeCakeTypeStage.close();
                enableButtons();
            }
        });

        Scene scene = new Scene(grid, 300, 100);
        removeCakeTypeStage.setScene(scene);

        return removeCakeTypeStage;
    }

    private void quitButtonClicked() {
    	Alert alert = AlertUtil.createAlert(AlertType.CONFIRMATION, "Confirm Quit", "Are you sure you want to quit?");
        Optional result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            db.closeConn();
            Platform.exit();
        }
    }

    public static void main(String[] args){
        launch(args);
    }
}