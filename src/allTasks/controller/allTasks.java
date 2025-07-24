package allTasks.controller;

import EditPage.controller.editpage;
import addTaskPage.controller.addTask;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
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

    public Button logOut;
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

    private FilteredList<ToDoItem> filteredList;


    private int userId;

    public void setUserId(int userId) {
        this.userId = userId;
        System.out.println("User ID set to: " + userId);
        loadTasksFromDB();
    }

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

        markColumn.setCellFactory(column -> {
            CheckBoxTableCell<ToDoItem, Boolean> cell = new CheckBoxTableCell<>(index -> {
                if (index >= tableView.getItems().size()) return new SimpleBooleanProperty(false);

                ToDoItem task = tableView.getItems().get(index);
                BooleanProperty prop = task.markedProperty();

                prop.addListener((obs, oldVal, newVal) -> {
                    task.setStatus(newVal ? "Completed" : "Pending");
                    updateTaskMarkedInDB(task.getId(), newVal);
                    filterTasks(filterBtn.getValue() == null ? "All" : filterBtn.getValue());
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
    }

    private void updateTaskMarkedInDB(int taskId, boolean marked) {
        String sql = "UPDATE tasks SET status = ? WHERE taskId = ? AND userId = ?";
        String status = marked ? "Completed" : "Pending";

        try (Connection conn = connectDB();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, taskId);
            stmt.setInt(3, userId);
            stmt.executeUpdate();
        } catch (Exception e) {
            System.out.println("❌ Failed to update task status in DB: " + e.getMessage());
        }
    }

    private void loadTasksFromDB() {
        System.out.println(userId);
        try (Connection conn = connectDB();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM tasks WHERE userId = ?")) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            taskList.clear();
            while (rs.next()) {
                int id = rs.getInt("taskId");
                String title = rs.getString("title");
                String status = rs.getString("status");
                String description = rs.getString("description");
                String dueDate = rs.getString("dueDate");
                ToDoItem task = new ToDoItem(id, userId, title, status, description, dueDate);
                task.setMarked("Completed".equalsIgnoreCase(status));
                taskList.add(task);
            }
            filteredList = new FilteredList<>(taskList, task -> true);
            tableView.setItems(filteredList);

            filterTasks(filterBtn.getValue() == null ? "All" : filterBtn.getValue());

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
        if (filteredList == null) return;

        filteredList.setPredicate(task -> {
            return switch (filter) {
                case "Completed" -> task.getStatus().equalsIgnoreCase("Completed");
                case "Pending" -> task.getStatus().equalsIgnoreCase("Pending");
                case "Overdue" -> isOverdue(task) && task.getStatus().equalsIgnoreCase("Pending");
                default -> true;
            };
        });
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
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    Button editIcon = createIconButton("/Images/pen-to-square-solid.png");
                    Button deleteIcon = createIconButton("/Images/trash-solid.png");
                    HBox container = new HBox(8, editIcon, deleteIcon);
                    container.setAlignment(Pos.CENTER);

                    ToDoItem task = (ToDoItem) getTableRow().getItem();

                    editIcon.setCursor(javafx.scene.Cursor.HAND);
                    editIcon.setTooltip(new Tooltip("Edit Task"));
                    deleteIcon.setTooltip(new Tooltip("Delete Task"));
                    deleteIcon.setCursor(javafx.scene.Cursor.HAND);

                    deleteIcon.setOnMouseClicked(e -> deleteTask(task));
                    editIcon.setOnMouseClicked(e -> {
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

                    setGraphic(container);
                }
            }
        });
    }

    private void deleteTask(ToDoItem task) {
        try (Connection conn = connectDB();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM tasks WHERE taskId = ?")) {
            stmt.setInt(1, task.getId());
            stmt.executeUpdate();
            taskList.remove(task);
            filterTasks(filterBtn.getValue() == null ? "All" : filterBtn.getValue());
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
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/addTaskPage/view/addTask.fxml"));
        Parent addTaskRoot = loader.load();
        addTask controller = loader.getController();
        controller.setUserId(userId);
        Scene scene = ((Node) event.getSource()).getScene();
        scene.setRoot(addTaskRoot);
    }

    @FXML
    public void logOut(ActionEvent event) throws IOException {
        userId = 0;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/landingPage/view/landingPage.fxml"));
        Parent LandingPage = loader.load();

        Scene scene = ((Node) event.getSource()).getScene();
        scene.setRoot(LandingPage);
    }


    public static class ToDoItem {
        private int id;
        private int userId;
        private final SimpleStringProperty task = new SimpleStringProperty();
        private final SimpleStringProperty status = new SimpleStringProperty();
        private final SimpleStringProperty description = new SimpleStringProperty();
        private final SimpleStringProperty dueDate = new SimpleStringProperty();
        private final SimpleBooleanProperty marked = new SimpleBooleanProperty(false);

        public ToDoItem(int id, int userId, String task, String status, String description, String dueDate) {
            this.id = id;
            this.userId = userId;
            this.task.set(task);
            this.status.set(status);
            this.description.set(description);
            this.dueDate.set(dueDate);
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
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
