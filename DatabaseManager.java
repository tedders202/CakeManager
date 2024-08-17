import java.sql.*;
import java.time.LocalDate;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Alert.AlertType;

public class DatabaseManager {
	private Connection conn;
	
	public DatabaseManager() throws ClassNotFoundException {
		try {
    		Class.forName("com.mysql.cj.jdbc.Driver");
    		conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/cake_orders", "root", "OracleD@t@:3");
        } catch (SQLException e) {
        	Alert alert = AlertUtil.createAlert(AlertType.ERROR, "Connection Error", "I'm sorry but something went wrong with the connection. Please retry again later.");
        	alert.showAndWait();
            System.exit(1); // Exit the program if we can't connect to the database
        }
	}
	
	public void getCakeTypes(ComboBox cakeTypeComboBox) {
		Statement stmt;
		try {
			stmt = conn.createStatement();
			ResultSet results = stmt.executeQuery("SELECT * FROM cake_types");

		       while (results.next()) {
		           cakeTypeComboBox.getItems().add(results.getString("name"));
		       }
		} catch (SQLException e) {
			Alert alert = AlertUtil.createAlert(AlertType.ERROR, "Cake Type Error", "There was an issue retrieving all the cake types. Please try again later");
			alert.showAndWait();
		}
	}
	
	public void takeOrder(String name, LocalDate dueDate, String cakeType) {
		try (PreparedStatement stmt2 = conn.prepareStatement("INSERT INTO orders (customer_name, due_date, cake_type) VALUES (?, ?, ?)");){
            stmt2.setString(1, name);
            stmt2.setDate(2, Date.valueOf(dueDate));
            stmt2.setString(3, cakeType);
            stmt2.executeUpdate();
            
            // Show a success message to the user
            Alert alert = AlertUtil.createAlert(AlertType.INFORMATION, "Order Successfully Placed", "Your order has been successfully placed!");
            alert.showAndWait();

        } catch (SQLIntegrityConstraintViolationException ex) {
            Alert alert = AlertUtil.createAlert(AlertType.ERROR, "Duplicate Order", "An order with the same ID already exists.");
            alert.showAndWait();
        } catch (SQLDataException ex) {
            Alert alert = AlertUtil.createAlert(AlertType.ERROR, "Invalid Data", "Invalid data was entered. Please check your input.");
            alert.showAndWait();
        } catch (SQLException ex) {
        	Alert alert = AlertUtil.createAlert(AlertType.ERROR, "Take Order Error", "We apologize, but there seems to be an unknown error submitting your order. Please try again later.");
            alert.showAndWait();
        }
	}
	
	public void markOrderPaid(int orderId) {
		try {
        	PreparedStatement stmt = conn.prepareStatement("UPDATE orders SET status = 'paid' WHERE id = ?");
            stmt.setInt(1, orderId);
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected == 0) {
            	Alert alert = AlertUtil.createAlert(AlertType.ERROR, "Invalid Order ID", "No order found with the given ID");
            	alert.showAndWait();
            } else {
            	Alert alert = AlertUtil.createAlert(AlertType.INFORMATION, "Order Successfully Paid", "Your order has been successfully paid!");
                alert.showAndWait();
            }
        } catch (SQLDataException ex) {
            Alert alert = AlertUtil.createAlert(AlertType.ERROR, "Invalid Order ID", "The order ID you entered is invalid. Please check the ID and try again.");
            alert.showAndWait();
        } catch (SQLTransactionRollbackException ex) {
            Alert alert = AlertUtil.createAlert(AlertType.ERROR, "Error Updating Order", "An error occurred while updating the order. Please try again later.");
            alert.showAndWait();
        } catch (SQLException ex) {
            Alert alert = AlertUtil.createAlert(AlertType.ERROR, "Error Marking Order as Paid", "An error occurred while marking the order as paid. Please try again later.");
            alert.showAndWait();
        }
	}
	
	public void checkDates() {
    	try (PreparedStatement stmtPaid = conn.prepareStatement("SELECT * FROM orders WHERE due_date BETWEEN ? AND ? AND status = 'paid'");
   	         PreparedStatement stmtUnpaid = conn.prepareStatement("SELECT * FROM orders WHERE due_date BETWEEN ? AND ? AND status != 'paid'")) {

   	        // Set the query parameters to the current date plus a certain number of days (e.g., 7)
   	        stmtPaid.setDate(1, Date.valueOf(LocalDate.now()));
   	        stmtPaid.setDate(2, Date.valueOf(LocalDate.now().plusDays(7)));
   	        stmtUnpaid.setDate(1, Date.valueOf(LocalDate.now()));
   	        stmtUnpaid.setDate(2, Date.valueOf(LocalDate.now().plusDays(7)));

   	        // Execute the queries and get the results
   	        ResultSet resultsPaid = stmtPaid.executeQuery();
   	        ResultSet resultsUnpaid = stmtUnpaid.executeQuery();

   	        // Create a string to store the results
   	        StringBuilder dueDatesString = new StringBuilder();

   	        // Append the paid orders to the string
   	        dueDatesString.append("Paid Orders:\n");
   	        while (resultsPaid.next()) {
   	            dueDatesString.append("Order ID: ").append(resultsPaid.getInt("id")).append("\n");
   	            dueDatesString.append("Customer Name: ").append(resultsPaid.getString("customer_name")).append("\n");
   	            dueDatesString.append("Due Date: ").append(resultsPaid.getDate("due_date")).append("\n");
   	            dueDatesString.append("Cake Type: ").append(resultsPaid.getString("cake_type")).append("\n\n");
   	        }

   	        // Append a separator between the paid and unpaid sections
   	        dueDatesString.append("\n------------------------\n\n");

   	        // Append the unpaid orders to the string
   	        dueDatesString.append("Unpaid Orders:\n");
   	        while (resultsUnpaid.next()) {
   	            dueDatesString.append("Order ID: ").append(resultsUnpaid.getInt("id")).append("\n");
   	            dueDatesString.append("Customer Name: ").append(resultsUnpaid.getString("customer_name")).append("\n");
   	            dueDatesString.append("Due Date: ").append(resultsUnpaid.getDate("due_date")).append("\n");
   	            dueDatesString.append("Cake Type: ").append(resultsUnpaid.getString("cake_type")).append("\n\n");
   	        }

   	        // Show the results in an alert dialog
   	        Alert alert = AlertUtil.createAlert(AlertType.INFORMATION, "Due Dates", "Orders Due Soon");
   	        alert.setContentText(dueDatesString.toString());
   	        alert.showAndWait();

   	    } catch (SQLDataException ex) {
   	        Alert alert = AlertUtil.createAlert(AlertType.ERROR, "Invalid Date Range", "The date range you entered is invalid. Please check the dates and try again.");
   	        alert.showAndWait();
   	    } catch (SQLTimeoutException ex) {
   	        Alert alert = AlertUtil.createAlert(AlertType.ERROR, "Query Timed Out", "The query timed out. Please try again later.");
   	        alert.showAndWait();
   	    } catch (SQLException ex) {
   	        Alert alert = AlertUtil.createAlert(AlertType.ERROR, "Error Checking Due Dates", "An error occurred while checking the due dates. Please try again later.");
   	        alert.showAndWait();
   	    }
	}
	
	public void completeOrder(int orderId) {
		try {
            PreparedStatement stmt = conn.prepareStatement("UPDATE orders SET status = 'completed' WHERE id = ?");
            stmt.setInt(1, orderId);
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected == 0) {
                Alert alert = AlertUtil.createAlert(AlertType.ERROR, "Invalid Order ID", "No order found with the given ID");
                alert.showAndWait();
            } else {
                // Show a success message to the user
                Alert alert = AlertUtil.createAlert(AlertType.INFORMATION, "Order Successfully Completed", "Your order has been successfully completed!");
                alert.showAndWait();
            }
        } catch (SQLDataException ex) {
            Alert alert = AlertUtil.createAlert(AlertType.ERROR, "Invalid Order ID", "The order ID you entered is invalid. Please check the ID and try again.");
            alert.showAndWait();
        } catch (SQLTransactionRollbackException ex) {
            Alert alert = AlertUtil.createAlert(AlertType.ERROR, "Error Updating Order", "An error occurred while updating the order. Please try again later.");
            alert.showAndWait();
        } catch (SQLException ex) {
            Alert alert = AlertUtil.createAlert(AlertType.ERROR, "Error Completing Order", "An error occurred while completing the order. Please try again later.");
            alert.showAndWait();
        }
	}
	
	public void addCake(String name, double price, String writing) {
		try {
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO cake_types (name, price, writing) VALUES (?, ?, ?)");
            stmt.setString(1, name);
            stmt.setDouble(2, price);
            stmt.setString(3, writing);
            stmt.executeUpdate();

            // Show a success message
            Alert alert = AlertUtil.createAlert(AlertType.INFORMATION, "Cake Type Added", "The new cake type has been added successfully!");
            alert.showAndWait();
        } catch (SQLIntegrityConstraintViolationException ex) {
            Alert alert = AlertUtil.createAlert(AlertType.ERROR, "Cake Type Already Exists", "A cake type with the same name already exists. Please try again with a different name.");
            alert.showAndWait();
        } catch (SQLDataException ex) {
            Alert alert = AlertUtil.createAlert(AlertType.ERROR, "Invalid Data", "The data you entered is invalid. Please check the data and try again.");
            alert.showAndWait();
        } catch (SQLException ex) {
            Alert alert = AlertUtil.createAlert(AlertType.ERROR, "Error Adding Cake Type", "An error occurred while adding the cake type. Please try again later.");
            alert.showAndWait();
        }
	}
	
	public void updateCake(double price, String writing, String cakeType) {
		try {
            PreparedStatement stmt = conn.prepareStatement("UPDATE cake_types SET price = IFNULL(?, price), writing = IFNULL(?, writing) WHERE name = ?");
            if (price != 0) {
                stmt.setDouble(1, price);
            } else {
                stmt.setNull(1, Types.DOUBLE);
            }
            if (writing.equalsIgnoreCase("Nothing")) {
                stmt.setString(2, "");
            } else if (!writing.isEmpty()) {
                stmt.setString(2, writing);
            } else {
                stmt.setString(2, null); // or stmt.setNull(2, Types.VARCHAR);
            }
            stmt.setString(3, cakeType);
            stmt.executeUpdate();

            // Show a success message
            Alert alert = AlertUtil.createAlert(AlertType.INFORMATION, "Cake Type Updated", "The cake type has been updated successfully!");
            alert.showAndWait();
        } catch (SQLDataException ex) {
            Alert alert = AlertUtil.createAlert(AlertType.ERROR, "Invalid Data", "The data you entered is invalid. Please check the data and try again.");
            alert.showAndWait();
        } catch (SQLTransactionRollbackException ex) {
            Alert alert = AlertUtil.createAlert(AlertType.ERROR, "Error Updating Cake Type", "An error occurred while updating the cake type. Please try again later.");
            alert.showAndWait();
        } catch (SQLException ex) {
            Alert alert = AlertUtil.createAlert(AlertType.ERROR, "Error Updating Cake Type", "An error occurred while updating the cake type. Please try again later.");
            alert.showAndWait();
        }
	}
	
	public void removeCake(String cakeType) {
		try {
            PreparedStatement stmt = conn.prepareStatement("DELETE FROM cake_types WHERE name = ?");
            stmt.setString(1, cakeType);
            stmt.executeUpdate();

            // Show a success message
            Alert alert = AlertUtil.createAlert(AlertType.INFORMATION, "Cake Type Removed", "The cake type has been removed successfully!");
            alert.showAndWait();
        } catch (SQLTransactionRollbackException ex) {
            Alert alert = AlertUtil.createAlert(AlertType.ERROR, "Error Removing Cake Type", "An error occurred while removing the cake type. Please try again later.");
            alert.showAndWait();
        } catch (SQLException ex) {
            Alert alert = AlertUtil.createAlert(AlertType.ERROR, "Error Removing Cake Type", "An error occurred while removing the cake type. Please try again later.");
            alert.showAndWait();
        }
	}
	
	public void closeConn() {
		try {
			conn.close();
		} catch (SQLException e) {
			Alert alert = AlertUtil.createAlert(AlertType.ERROR, "Error", "An error has occurred when trying to close.");
			alert.showAndWait();
		}
	}
}
