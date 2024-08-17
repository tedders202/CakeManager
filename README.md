Java program made for a cake bakery that wants to store its menu and all of its orders into a centralized database.

Connects the GUI application to a MySQL server that has two different tables which store the different cakes available and the orders gathered.

SQL table structures:

database: cake_orders

Table: cake_types
Columns:
name varchar(255) PK 
price double 
writing varchar(255)

Table: orders
Columns:
id int AI PK 
customer_name varchar(255) 
cake_type varchar(255) 
order_date date 
due_date date 
status enum('pending','paid','completed')

Ran through Eclipse IDE, preliminary steps in order to connect to MySQL server and optimize JavaFX library.

1) Download MySQL connector jar file and add to classpath of the project
2) Download JavaFX library, add to User Library, and add to classpath of the project
3) Run configurations and change VM arguments. Copy and paste below, replace file_path with the file path to the JavaFX library that was downloaded
   --module-path "file_path" --add-modules javafx.controls,javafx.graphics,javafx.fxml
4) While running configurations, in Dependencies, make sure both JavaFX and Java FX SDK are both in module path entries and mysql connector jar file is in classpath entries.
