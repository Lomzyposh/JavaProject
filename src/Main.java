import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

<<<<<<< HEAD
public class Main extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/addTaskPage/view/addTask.fxml"));
        Parent root = loader.load();



        stage.setScene(new Scene(root));
        stage.setTitle("Edit Task - Test");
=======
import java.util.Objects;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/LoginPage/view/login.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setTitle("User Login");
        stage.setScene(scene);

>>>>>>> feature-alamin-login
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
