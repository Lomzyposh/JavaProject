package SignUpPage.controller;

import allTasks.controller.allTasks;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class signUpController implements Initializable {

//    Calling by Ids

    @FXML
    public VBox signUpForm;
    @FXML
    public AnchorPane rootPane;
    @FXML
    public Button fadeTop;

    @FXML
    public PasswordField passwordField;
    @FXML
    public PasswordField confirmPasswordField;
    @FXML
    public TextField usernameField;


    //    Show Alert Method
    public void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setTitle("Notification");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    //    Go to Login Page
    public void goToLogin(ActionEvent event) throws IOException {
        Parent loginRoot = FXMLLoader.load(getClass().getResource("/LoginPage/view/login.fxml"));
        Scene scene = ((Node) event.getSource()).getScene();
        scene.setRoot(loginRoot);
    }

    @FXML
    public void continueAsGuest(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/allTasks/view/allTasks.fxml"));
            Parent allTasksRoot = loader.load();
            allTasks controller = loader.getController();
            controller.setUserId(0);
            Scene scene = ((Node) event.getSource()).getScene();
            scene.setRoot(allTasksRoot);
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Failed to load All Tasks page.");
            e.printStackTrace();
            return;
        }
    }

    @FXML
    public void navigateToPreview(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/landingPage/view/landingPage.fxml"));
        Parent LandingPage = loader.load();

        Scene scene = ((Node) event.getSource()).getScene();
        scene.setRoot(LandingPage);
    }


    //    Animation for the Sign-Up Page
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        fadeTop.setTranslateY(-50);
        fadeTop.setOpacity(0);

        signUpForm.setTranslateX(200);
        signUpForm.setOpacity(0);

        TranslateTransition slide = new TranslateTransition(Duration.seconds(1), signUpForm);
        slide.setFromX(200);
        slide.setToX(0);

        FadeTransition fade = new FadeTransition(Duration.seconds(1), signUpForm);
        fade.setFromValue(0);
        fade.setToValue(1);

        ParallelTransition parallelTransition = new ParallelTransition(slide, fade);
        parallelTransition.play();

        parallelTransition.setOnFinished(e -> {
            TranslateTransition drop = new TranslateTransition(Duration.seconds(0.5), fadeTop);
            drop.setFromY(-50);
            drop.setToY(0);

            FadeTransition tFade = new FadeTransition(Duration.seconds(0.5), fadeTop);
            tFade.setFromValue(0);
            tFade.setToValue(1);

            ParallelTransition animation = new ParallelTransition(drop, tFade);
            animation.play();
        });
    }

    //    Once u click the Sign-Up Button, it will submit this
    @FXML
    public void submitForm(ActionEvent event) {

//        Get Users Values
        String userInput = usernameField.getText();
        String userPassword = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();


//        Connect to the database
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            String user = "sa2";
            String pass = "00000000";
            String url = "jdbc:sqlserver://localhost:1433;databaseName=To_DO_App;encrypt=true;trustServerCertificate=true";

            Connection conn = DriverManager.getConnection(url, user, pass);

//            Check if empty
            if (userInput.isEmpty() || userPassword.isEmpty() || confirmPassword.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Please fill in all fields.");
                return;
//                1293926743
            }

            if (userPassword.length() < 6 || userPassword.length() > 20) {
                showAlert(Alert.AlertType.WARNING, "Password must be between 6 and 20 characters.");
                return;
            }

            if (!userPassword.equals(confirmPassword)) {
                showAlert(Alert.AlertType.ERROR, "Passwords do not match.");
                return;
            }
            if (userInput.length() < 3 || userInput.length() > 20) {
                showAlert(Alert.AlertType.WARNING, "Username must be between 3 and 20 characters.");
                return;
            }


//            Code to check if the username already exists
            String checkQuery = "SELECT COUNT(*) FROM users WHERE username = ?";
            PreparedStatement checkStatement = conn.prepareStatement(checkQuery);
            checkStatement.setString(1, userInput);
            ResultSet resultSet = checkStatement.executeQuery();

            if (resultSet.next() && resultSet.getInt(1) > 0) {
                System.out.println("Username already exists. Please choose a different username.");
                showAlert(Alert.AlertType.WARNING, "Username already exists. Please choose a different username.");
                return;
            }

//            Insert the user into the database
            String query = "INSERT INTO users (username, password) VALUES (?,?)";

            PreparedStatement preparedStatement = conn.prepareStatement(query);

            preparedStatement.setString(1, userInput);
            preparedStatement.setString(2, userPassword);


            Boolean rs = preparedStatement.execute();
            System.out.println("Success");
            showAlert(Alert.AlertType.INFORMATION, "Registration successful! You can now log in.");

            goToLogin(event);

        } catch (ClassNotFoundException | SQLException | IOException e) {
            System.out.println(e.getMessage());
        }
    }

}
