package addTaskPage.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;

import java.io.IOException;

public class addTask {

    @FXML
    public void navigateToAllTask(javafx.event.ActionEvent event) throws IOException {
        Parent addTaskRoot = FXMLLoader.load(getClass().getResource("/allTasks/view/allTasks.fxml"));
        Scene scene = ((Node) event.getSource()).getScene();
        scene.setRoot(addTaskRoot);
    }




    public void handleAddTask(ActionEvent actionEvent) {
    }

    public void handleMarkDone(ActionEvent actionEvent) {
    }

    public void handleDeleteTask(ActionEvent actionEvent) {
    }
}
