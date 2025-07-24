package LoginPage.controller;

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
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.Optional;
import java.util.ResourceBundle;


public class loginController implements Initializable {

    @FXML
    public VBox loginForm;
    @FXML
    public TextField usernameField;
    @FXML
    public PasswordField passwordField;
    @FXML
    public CheckBox rememberMe;
    @FXML
    public AnchorPane rootPane;

    public void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setTitle("Notification");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        for (Node btn : rootPane.lookupAll(".fadeTop")) {
            btn.setTranslateY(-50);
            btn.setOpacity(0);
        }
        loginForm.setTranslateX(-250);
        loginForm.setOpacity(0);

        TranslateTransition slide = new TranslateTransition(Duration.seconds(1), loginForm);
        slide.setFromX(-250);
        slide.setToX(0);

        FadeTransition fade = new FadeTransition(Duration.seconds(1), loginForm);
        fade.setFromValue(0);
        fade.setToValue(1);

        ParallelTransition loginFormIntro = new ParallelTransition(fade, slide);
        loginFormIntro.play();

        loginFormIntro.setOnFinished(e -> {
            int delay = 0;

            for (Node btn : rootPane.lookupAll(".fadeTop")) {
                TranslateTransition drop = new TranslateTransition(Duration.seconds(0.5), btn);
                drop.setFromY(-50);
                drop.setToY(0);
                drop.setDelay(Duration.millis(delay));

                FadeTransition tFade = new FadeTransition(Duration.seconds(0.5), btn);
                tFade.setFromValue(0);
                tFade.setToValue(1);
                tFade.setDelay(Duration.millis(delay));

                ParallelTransition animation = new ParallelTransition(drop, tFade);
                animation.play();

                delay += 150;
            }
        });
    }


    @FXML
    private void handleLogin(ActionEvent event) {
        String userInput = usernameField.getText();
        String passInput = passwordField.getText();

        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            String user = "sa2";
            String pass = "00000000";
            String url = "jdbc:sqlserver://localhost:1433;databaseName=To_DO_App;encrypt=true;trustServerCertificate=true";

            Connection conn = DriverManager.getConnection(url, user, pass);

            if (userInput.isEmpty() || passInput.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Username and Password cannot be empty.");
                return;
            }

            String checkQuery = "SELECT * FROM users WHERE username = ? AND password = ?";
            PreparedStatement checkStatement = conn.prepareStatement(checkQuery);
            checkStatement.setString(1, userInput);
            checkStatement.setString(2, passInput);
            ResultSet resultSet = checkStatement.executeQuery();

            if (resultSet.next()) {
                System.out.println("Login successful!");
                showAlert(Alert.AlertType.INFORMATION, "Login successful!");
                // Redirect to All Tasks page
                // Redirect to All Tasks page and pass userId
                int userId = resultSet.getInt("userId");
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/allTasks/view/allTasks.fxml"));
                    Parent allTasksRoot = loader.load();
                    allTasks controller = loader.getController();
                    controller.setUserId(userId);
                    Scene scene = ((Node) event.getSource()).getScene();
                    scene.setRoot(allTasksRoot);
                } catch (IOException e) {
                    showAlert(Alert.AlertType.ERROR, "Failed to load All Tasks page.");
                    e.printStackTrace();
                    return;
                }

            } else {
                showAlert(Alert.AlertType.ERROR, "Invalid Username or Password.");
            }


        } catch (ClassNotFoundException | SQLException e) {
            System.out.println(e.getMessage());
        }


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


    public void handleSignUp(ActionEvent event) throws IOException {
        Parent signUpRoot = FXMLLoader.load(getClass().getResource("/SignUpPage/view/signup.fxml"));
        Scene scene = ((Node) event.getSource()).getScene();
        scene.setRoot(signUpRoot);
    }

    public void goToPreviewPage(ActionEvent actionEvent) {
        try {
            Parent previewRoot = FXMLLoader.load(getClass().getResource("/LandingPage/view/landingPage.fxml"));
            Scene scene = ((Node) actionEvent.getSource()).getScene();
            scene.setRoot(previewRoot);
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Failed to load Preview page.");
            e.printStackTrace();
        }
    }


//    public static void main(String[] args) {
//        URL url = regController.class.getResource("/user.webp");
//        System.out.println(url);
//    }
}
