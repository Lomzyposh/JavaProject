////package addTaskPage.controller;
////
////import javafx.event.ActionEvent;
////import javafx.fxml.FXML;
////import javafx.fxml.FXMLLoader;
////import javafx.scene.Node;
////import javafx.scene.Parent;
////import javafx.scene.Scene;
////import javafx.scene.control.TextField;
////
////import java.io.IOException;
////
////public class addTask {
////
////    @FXML
////    public void navigateToAllTask(javafx.event.ActionEvent event) throws IOException {
////        Parent addTaskRoot = FXMLLoader.load(getClass().getResource("/allTasks/view/allTasks.fxml"));
////        Scene scene = ((Node) event.getSource()).getScene();
////        scene.setRoot(addTaskRoot);
////    }
////
////
////
////
////    public void handleAddTask(ActionEvent actionEvent) {
////    }
////
////    public void handleMarkDone(ActionEvent actionEvent) {
////    }
////
////    public void handleDeleteTask(ActionEvent actionEvent) {
////    }
////}
//package addTaskPage.controller;
//
//import javafx.event.ActionEvent;
//import javafx.fxml.FXML;
//import javafx.fxml.FXMLLoader;
//import javafx.scene.Node;
//import javafx.scene.Parent;
//import javafx.scene.Scene;
//import javafx.scene.control.DatePicker;
//import javafx.scene.control.TextField;
//
//import java.io.IOException;
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.PreparedStatement;
//import java.sql.SQLException;
//import java.time.LocalDate;
//
//public class addTask {
//    @FXML
//    private TextField taskInput;
//
//    @FXML
//    private TextField descriptionInput;
//
//    @FXML
//    private DatePicker dueDatePicker;
//
//    @FXML
//    public void navigateToAllTask(ActionEvent event) throws IOException {
//        Parent addTaskRoot = FXMLLoader.load(getClass().getResource("/allTasks/view/allTasks.fxml"));
//        Scene scene = ((Node) event.getSource()).getScene();
//        scene.setRoot(addTaskRoot);
//    }
//
//
//    @FXML
//    public void handleAddTask(ActionEvent event) {
//        String title = taskInput.getText();
//        String description = descriptionInput.getText();
//        LocalDate dueDate = dueDatePicker.getValue();
//
//        try {
//            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
//            String user = "sa2", pass = "123456";
//            String url = "jdbc:sqlserver://localhost:1433;databaseName=To_DO_App;trustServerCertificate=true";
//
//            Connection conn = DriverManager.getConnection(url, user, pass);
//            String query = "INSERT INTO tasks(userId, title, description, dueDate) VALUES (?, ?, ?)";
//            PreparedStatement stmt = conn.prepareStatement(query);
//            stmt.setInt(1, 1); // Static user ID for now
//            stmt.setString(2, title);
//            stmt.setString(3, description);
//            stmt.setString(4, dueDate.toString());
//
//            stmt.executeUpdate();
//            System.out.println("✅ Task inserted into database.");
//        } catch (ClassNotFoundException | SQLException e) {
//            System.out.println("❌ Error: " + e.getMessage());
//        }
//    }
//}
package addTaskPage.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

public class addTask {

    @FXML private TextField taskInput;
    @FXML private TextField descriptionInput;
    @FXML private DatePicker dueDatePicker;

    // Redirect to All Tasks page
    @FXML
    public void navigateToAllTask(ActionEvent event) throws IOException {
        Parent allTaskRoot = FXMLLoader.load(getClass().getResource("/allTasks/view/allTasks.fxml"));
        Scene scene = ((Node) event.getSource()).getScene();
        scene.setRoot(allTaskRoot);
    }

    // Add task to database
    @FXML
    public void handleAddTask(ActionEvent event) {
        String title = taskInput.getText();
        String description = descriptionInput.getText();
        LocalDate dueDate = dueDatePicker.getValue();

        if (title.isEmpty() || dueDate == null) {
            System.out.println("⚠️ Title and Due Date are required.");
            return;
        }

        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            String user = "sa2", pass = "123456";
            String url = "jdbc:sqlserver://localhost:1433;databaseName=To_DO_App;trustServerCertificate=true";

            Connection conn = DriverManager.getConnection(url, user, pass);
            String query = "INSERT INTO tasks(userId, title, description, dueDate, status) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, 1); // static userId
            stmt.setString(2, title);
            stmt.setString(3, description);
            stmt.setString(4, dueDate.toString());
            stmt.setString(5, "Pending"); // Default status

            stmt.executeUpdate();
            System.out.println("✅ Task successfully added to database.");

            // Optionally navigate back
            navigateToAllTask(event);

        } catch (ClassNotFoundException | SQLException | IOException e) {
            System.out.println("❌ Error adding task: " + e.getMessage());
        }
    }
}
