package allTasks.controller;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;

import java.awt.event.ActionEvent;
import java.io.IOException;

public class allTasks {

    @FXML
    private TableView<ToDoItem> tableView;
    @FXML
    private TableColumn<ToDoItem, String> TitleColumn;
    @FXML
    private TableColumn<ToDoItem, String> StatusColumn;
    @FXML
    private TableColumn<ToDoItem, Void> actionColumn;
    @FXML
    private ComboBox<String> filterBtn;
    @FXML
    public TableColumn<ToDoItem, Boolean> markColumn;



    @FXML
    public void initialize() {
        markColumn.setCellValueFactory(cellData -> cellData.getValue().markedProperty());
        markColumn.setCellFactory(tc -> new CheckBoxTableCell<>());

        setupActionColumn();

        ObservableList<String> items = FXCollections.observableArrayList("Pending", "Completed", "Overdue");
        filterBtn.setItems(items);

        filterBtn.setOnAction(event -> {
            String selected = filterBtn.getValue();
            System.out.println("Selected: " + selected);
        });

        TitleColumn.setCellValueFactory(new PropertyValueFactory<>("task"));
        StatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        TitleColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item);
                setAlignment(Pos.CENTER);
            }
        });

        StatusColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item);
                setAlignment(Pos.CENTER);
            }
        });

        ObservableList<ToDoItem> toDoList = FXCollections.observableArrayList(
                new ToDoItem("Buy groceries", "Pending"),
                new ToDoItem("Wash car", "Done"),
                new ToDoItem("Read book", "Pending"),
                new ToDoItem("Call Mom", "Done")
        );

        tableView.setItems(toDoList);
    }

    // ✅ FontAwesome icon setup for action column
    private void setupActionColumn() {
        actionColumn.setCellFactory(column -> new TableCell<ToDoItem, Void>() {

            private final Button editIcon = createIconButton("/Images/pen-to-square-solid.png");

            private final Button deleteIcon = createIconButton("/Images/trash-solid.png");
            private final HBox container = new HBox(8, editIcon, deleteIcon);

            {
                editIcon.getStyleClass().addAll("edit-icon", "actionBtn");
                deleteIcon.getStyleClass().addAll("delete-icon", "actionBtn");
//                editIcon.setIconSize(18);
//                deleteIcon.setIconSize(18);
//                editIcon.setStyle("-fx-icon-color: #4A90E2; -fx-cursor: hand;");
//                deleteIcon.setStyle("-fx-icon-color: #DC143C; -fx-cursor: hand;");

                Tooltip.install(editIcon, new Tooltip("Edit Task"));
                Tooltip.install(deleteIcon, new Tooltip("Delete Task"));

                editIcon.setOnMouseClicked(e -> {
                    ToDoItem task = getTableView().getItems().get(getIndex());
                    System.out.println("Edit: " + task.getTask());
                });

                deleteIcon.setOnMouseClicked(e -> {
                    ToDoItem task = getTableView().getItems().get(getIndex());
                    System.out.println("Delete: " + task.getTask());
                });

                container.setAlignment(Pos.CENTER);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : container);
            }
        });
    }

    @FXML
    public void navigateToAddTask(javafx.event.ActionEvent event) throws IOException{
        Parent addTaskRoot = FXMLLoader.load(getClass().getResource("/addTaskPage/view/addTask.fxml"));
        Scene scene = ((Node) event.getSource()).getScene();
        scene.setRoot(addTaskRoot);
    }

    public static class ToDoItem {
        private final SimpleStringProperty task = new SimpleStringProperty();
        private final SimpleStringProperty status = new SimpleStringProperty();
        private final SimpleBooleanProperty marked = new SimpleBooleanProperty(false);

        public ToDoItem(String task, String status) {
            this.task.set(task);
            this.status.set(status);
        }

        public String getTask() {
            return task.get();
        }

        public void setTask(String value) {
            task.set(value);
        }

        public String getStatus() {
            return status.get();
        }

        public void setStatus(String value) {
            status.set(value);
        }

        public boolean isMarked() {
            return marked.get();
        }

        public void setMarked(boolean value) {
            marked.set(value);
        }

        public BooleanProperty markedProperty() {
            return marked;
        }
    }

    private Button createIconButton(String resourcePath) {
        Image image = new Image(getClass().getResourceAsStream(resourcePath));
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(16);
        imageView.setFitWidth(16);

        Button button = new Button();
        button.setGraphic(imageView);
        button.setStyle("-fx-background-color: transparent;");
        return button;
    }
}
