package allTasks.controller;

import EditPage.controller.editpage;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.util.Objects;

public class allTasks {

    @FXML
    private TableView<ToDoItem> tableView;
    @FXML
    private TableColumn<ToDoItem, String> TitleColumn;
    @FXML
    private TableColumn<ToDoItem, String> StatusColumn;
    @FXML
    private TableColumn<ToDoItem, String> dueDateColumn;
    @FXML
    private TableColumn<ToDoItem, Void> actionColumn;
    @FXML
    private ComboBox<String> filterBtn;
    @FXML
    public TableColumn<ToDoItem, Boolean> markColumn;

    private final ObservableList<ToDoItem> taskList = FXCollections.observableArrayList();

    private Connection connectDB() throws SQLException, ClassNotFoundException {
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        String user = "sa2", pass = "00000000",
                url = "jdbc:sqlserver://localhost:1433;databaseName=TO_DO_App;encrypt=true;trustServerCertificate=true";
        return DriverManager.getConnection(url, user, pass);
    }

    @FXML
    public void initialize() {
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        TitleColumn.setCellValueFactory(new PropertyValueFactory<>("task"));
        StatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        dueDateColumn.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
        markColumn.setCellValueFactory(cellData -> cellData.getValue().markedProperty());

        markColumn.setCellFactory(tc -> {
            CheckBoxTableCell<ToDoItem, Boolean> cell = new CheckBoxTableCell<>(index -> {
                BooleanProperty prop = tableView.getItems().get(index).markedProperty();
                prop.removeListener((obs, oldVal, newVal) -> {
                });
                prop.addListener((obs, wasMarked, isNowMarked) -> {
                    ToDoItem item = tableView.getItems().get(index);
                    item.setStatus(isNowMarked ? "Completed" : "Pending");
                    filterTasks(isNowMarked ? "Completed" : "Pending");
                    updateTaskMarkedInDB(item.getId(), isNowMarked);
                    tableView.refresh();
                });
                return prop;
            });
            cell.setAlignment(Pos.CENTER);
            return cell;
        });

        filterBtn.setItems(FXCollections.observableArrayList("All", "Pending", "Completed", "Overdue"));
        filterBtn.setOnAction(e -> filterTasks(filterBtn.getValue()));

        setupActionColumn();
        loadTasksFromDB();
    }

    private void updateTaskMarkedInDB(int taskId, boolean marked) {
        String sql = "UPDATE tasks SET status = ? WHERE taskId = ?";
        String status = marked ? "Completed" : "Pending";

        try (Connection conn = connectDB();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, taskId);
            stmt.executeUpdate();
        } catch (Exception e) {
            System.out.println("❌ Failed to update task status in DB: " + e.getMessage());
        }
    }

    private void loadTasksFromDB() {
        try (Connection conn = connectDB();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM tasks WHERE userId = 1")) {
            ResultSet rs = stmt.executeQuery();
            taskList.clear();
            while (rs.next()) {
                int id = rs.getInt("taskId");
                String title = rs.getString("title");
                String status = rs.getString("status");
                String description = rs.getString("description");
                String dueDate = rs.getString("dueDate");
                ToDoItem task = new ToDoItem(id, title, status, description, dueDate);
                task.setMarked("Completed".equalsIgnoreCase(status));
                taskList.add(task);
            }
            tableView.setItems(taskList);

            tableView.setRowFactory(tv -> new TableRow<>() {
                @Override
                protected void updateItem(ToDoItem item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setStyle(""); // Default style
                    } else {
                        if ("Completed".equalsIgnoreCase(item.getStatus())) {
                            setStyle("-fx-background-color: #d4edda;");
                        } else {
                            setStyle("");
                        }
                    }
                }
            });

        } catch (Exception e) {
            System.out.println("❌ Failed to load tasks: " + e.getMessage());
        }
    }

    private void filterTasks(String filter) {
        ObservableList<ToDoItem> filtered = FXCollections.observableArrayList();
        for (ToDoItem task : taskList) {
            switch (filter) {
                case "Completed" -> {
                    if (task.getStatus().equalsIgnoreCase("Completed")) filtered.add(task);
                }
                case "Pending" -> {
                    if (task.getStatus().equalsIgnoreCase("Pending")) filtered.add(task);
                }
                case "Overdue" -> {
                    if (isOverdue(task) && task.getStatus().equalsIgnoreCase("Pending")) filtered.add(task);
                }
                default -> filtered.add(task);
            }
        }
        tableView.setItems(filtered);
    }


    private boolean isOverdue(ToDoItem task) {
        try {
            LocalDate due = LocalDate.parse(task.getDueDate().substring(0, 10));
            return due.isBefore(LocalDate.now());
        } catch (Exception e) {
            return false;
        }
    }

    private void setupActionColumn() {
        actionColumn.setCellFactory(column -> new TableCell<>() {
            private final Button editIcon = createIconButton("/Images/pen-to-square-solid.png");
            private final Button deleteIcon = createIconButton("/Images/trash-solid.png");
            private final HBox container = new HBox(8, editIcon, deleteIcon);

            {
                container.setAlignment(Pos.CENTER);
                editIcon.setCursor(javafx.scene.Cursor.HAND);
                editIcon.setTooltip(new Tooltip("Edit Task"));
                deleteIcon.setTooltip(new Tooltip("Delete Task"));
                deleteIcon.setCursor(javafx.scene.Cursor.HAND);

                deleteIcon.setOnMouseClicked(e -> {
                    ToDoItem task = getTableView().getItems().get(getIndex());
                    deleteTask(task);
                });
                editIcon.setOnMouseClicked(e -> {
                    ToDoItem task = getTableView().getItems().get(getIndex());
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/EditPage/view/EDITPAGE.fxml"));
                        Parent editRoot = loader.load();
                        editpage controller = loader.getController();
                        controller.setTaskData(task);
                        Scene scene = ((Node) e.getSource()).getScene();
                        scene.setRoot(editRoot);
                    } catch (IOException ex) {
                        System.out.println("❌ Failed to open edit page: " + ex.getMessage());
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : container);
            }
        });
    }

    private void deleteTask(ToDoItem task) {
        try (Connection conn = connectDB();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM tasks WHERE taskId = ?")) {
            stmt.setInt(1, task.getId());
            stmt.executeUpdate();
            taskList.remove(task);
            tableView.setItems(FXCollections.observableArrayList(taskList));
        } catch (Exception e) {
            System.out.println("❌ Error deleting task: " + e.getMessage());
        }
    }

    private Button createIconButton(String resourcePath) {
        Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream(resourcePath)));
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(16);
        imageView.setFitWidth(16);
        Button button = new Button();
        button.setGraphic(imageView);
        button.setStyle("-fx-background-color: transparent;");
        return button;
    }

    @FXML
    public void navigateToAddTask(javafx.event.ActionEvent event) throws IOException {
        Parent addTaskRoot = FXMLLoader.load(getClass().getResource("/addTaskPage/view/addTask.fxml"));
        Scene scene = ((Node) event.getSource()).getScene();
        scene.setRoot(addTaskRoot);
    }

    public static class ToDoItem {
        private final int id;
        private final SimpleStringProperty task = new SimpleStringProperty();
        private final SimpleStringProperty status = new SimpleStringProperty();
        private final SimpleStringProperty description = new SimpleStringProperty();
        private final SimpleStringProperty dueDate = new SimpleStringProperty();
        private final SimpleBooleanProperty marked = new SimpleBooleanProperty(false);

        public ToDoItem(int id, String task, String status, String description, String dueDate) {
            this.id = id;
            this.task.set(task);
            this.status.set(status);
            this.description.set(description);
            this.dueDate.set(dueDate);
        }

        public int getId() {
            return id;
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

        public String getDescription() {
            return description.get();
        }

        public void setDescription(String value) {
            description.set(value);
        }

        public String getDueDate() {
            return dueDate.get();
        }

        public void setDueDate(String value) {
            dueDate.set(value);
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
}
