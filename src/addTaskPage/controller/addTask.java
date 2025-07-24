package addTaskPage.controller;

import allTasks.controller.allTasks;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

public class addTask {

    @FXML
    private TextField taskInput;
    @FXML
    private TextField descriptionInput;
    @FXML
    private DatePicker dueDatePicker;

    private int userId;

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setTitle("Notification");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Redirect to All Tasks page
    @FXML
    public void navigateToAllTask(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/allTasks/view/allTasks.fxml"));
        Parent allTaskRoot = loader.load();

        allTasks controller = loader.getController();
        controller.setUserId(userId);

        Scene scene = ((Node) event.getSource()).getScene();
        scene.setRoot(allTaskRoot);

    }




    @FXML
    public void handleAddTask(ActionEvent event) {
        String title = taskInput.getText();
        String description = descriptionInput.getText();
        LocalDate dueDate = dueDatePicker.getValue();

        if (title.isEmpty() || dueDate == null) {
            System.out.println("⚠️ Title and Due Date are required.");
            showAlert(Alert.AlertType.WARNING, "⚠️ Please fill in all required fields.");
            return;
        }

        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            String user = "sa2", pass = "00000000";
            String url = "jdbc:sqlserver://localhost:1433;databaseName=To_DO_App;trustServerCertificate=true";

            Connection conn = DriverManager.getConnection(url, user, pass);
            String query = "INSERT INTO tasks(userId, title, description, dueDate, status) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, userId);
            stmt.setString(2, title);
            stmt.setString(3, description);
            stmt.setString(4, dueDate.toString());
            stmt.setString(5, "Pending");

            stmt.executeUpdate();
            System.out.println("✅ Task successfully added to database.");
//            showAlert(Alert.AlertType.INFORMATION, "✅ Task successfully added!");

            navigateToAllTask(event);

        } catch (ClassNotFoundException | SQLException | IOException e) {
            System.out.println("❌ Error adding task: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "❌ Failed to add task: ");
        }
    }
}
