package EditPage.controller;

import allTasks.controller.allTasks.ToDoItem;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;

public class editpage {

    @FXML
    private TextField taskInput;
    @FXML
    private TextField descriptionInput;
    @FXML
    private DatePicker dueDatePicker;

    private int taskId;

    public void setTaskData(ToDoItem task) {
        this.taskId = task.getId();
        taskInput.setText(task.getTask());
        descriptionInput.setText(task.getDescription());
        dueDatePicker.setValue(LocalDate.parse(task.getDueDate().substring(0, 10)));
    }

    @FXML
    public void handleSave(ActionEvent event) {
        String taskTitle = taskInput.getText();
        String description = descriptionInput.getText();
        LocalDate dueDate = dueDatePicker.getValue();

        if (taskTitle.isEmpty() || description.isEmpty() || dueDate == null) {
            showAlert(Alert.AlertType.WARNING, "Please fill in all required fields.");
            return;
        }

        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            String url = "jdbc:sqlserver://localhost:1433;databaseName=To_DO_App;trustServerCertificate=true";
            String user = "sa2", pass = "00000000";
            Connection conn = DriverManager.getConnection(url, user, pass);
            String query = "UPDATE tasks SET title = ?, description = ?, dueDate = ? WHERE taskId = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, taskTitle);
            stmt.setString(2, description);
            stmt.setString(3, dueDate.toString());
            stmt.setInt(4, taskId);
            stmt.executeUpdate();
            conn.close();
            navigateToAllTasks(event);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "❌ Failed to update task: ");
        }
    }

    @FXML
    public void handleCancel(ActionEvent event) throws IOException {
        navigateToAllTasks(event);
    }

    private void navigateToAllTasks(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/allTasks/view/allTasks.fxml"));
        Scene scene = ((Node) event.getSource()).getScene();
        scene.setRoot(root);
    }

    public void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setTitle("Notification");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
