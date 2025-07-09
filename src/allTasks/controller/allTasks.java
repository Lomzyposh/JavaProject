package allTasks.controller;

import javafx.application.Application;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class allTasks{

    @FXML
    public ListView<TaskItem> listView;

    public static class TaskItem {
        private final SimpleStringProperty text;
        private final SimpleBooleanProperty selected;

        public TaskItem(String text) {
            this.text = new SimpleStringProperty(text);
            this.selected = new SimpleBooleanProperty(false);
        }

        public String getText() {
            return text.get();
        }

        public SimpleStringProperty textProperty() {
            return text;
        }

        public boolean isSelected() {
            return selected.get();
        }

        public SimpleBooleanProperty selectedProperty() {
            return selected;
        }

    }

    @FXML
    public void initialize() {
        ObservableList<TaskItem> tasks = FXCollections.observableArrayList(
                new TaskItem("Go to the Mall"),
                new TaskItem("Go to the Mall"),
                new TaskItem("Go to the Mall"),
                new TaskItem("Go to the Mall")
        );

        listView.setItems(tasks);

        listView.setCellFactory(lv -> new ListCell<>() {
            private final CheckBox checkBox = new CheckBox();
            private final Label label = new Label();
            private final HBox hbox = new HBox(10, checkBox, label);

            {
                hbox.setAlignment(Pos.CENTER_LEFT);
            }

            @Override
            protected void updateItem(TaskItem taskItem, boolean empty) {
                super.updateItem(taskItem, empty);
                if (empty || taskItem == null) {
                    setGraphic(null);
                } else {
                    checkBox.selectedProperty().bindBidirectional(taskItem.selectedProperty());
                    label.textProperty().bind(taskItem.textProperty());
                    setGraphic(hbox);
                }
            }
        });
    }
}
