package LoginPage.controller;

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
    public Hyperlink forgotLink;
    @FXML
    public AnchorPane rootPane;

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


    public void showInfoAlert() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Login Info");
        alert.setHeaderText("Login Successful");
        alert.setContentText("Welcome back to Tasky!");
        alert.showAndWait();
    }


    public void showErrorAlert(String errorMsg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Login Failed");
        alert.setHeaderText("Something went wrong");
        alert.setContentText(errorMsg);
        alert.showAndWait();
    }


    public boolean showConfirmation(String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm");
        alert.setHeaderText("Are you sure?");
        alert.setContentText(message);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    @FXML
    private void handleLogin() {
        String user = usernameField.getText();
        String pass = passwordField.getText();

        if (user.isEmpty() || pass.isEmpty()) {
            showErrorAlert("Username or Password cannot be empty.");
        } else if (user.equals("admin") && pass.equals("1234")) {
            showInfoAlert();
        } else {
            showErrorAlert("Invalid credentials. Try again.");
        }

    }

    public void handleSignUp(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/SignUpPage/view/signup.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }


//    public static void main(String[] args) {
//        URL url = regController.class.getResource("/user.webp");
//        System.out.println(url);
//    }
}
