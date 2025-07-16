package SignUpPage.controller;

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
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class signUpController implements Initializable {

    @FXML
    public VBox signUpForm;
    @FXML
    public AnchorPane rootPane;
    @FXML
    public Button fadeTop;

    @FXML
    public TextField usernameField;
    @FXML
    public PasswordField passwordField;
    @FXML
    public PasswordField confirmPasswordField;

    public void goToLogin(ActionEvent event) throws IOException {
        Parent loginRoot = FXMLLoader.load(getClass().getResource("/LoginPage/view/login.fxml"));
        Scene scene = ((Node) event.getSource()).getScene();
        scene.setRoot(loginRoot);
    }

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


    @FXML
    public void submitForm() {

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

            String query = "INSERT INTO users (username, password) VALUES (?,?)";

            PreparedStatement preparedStatement = conn.prepareStatement(query);

            preparedStatement.setString(1, userInput);
            preparedStatement.setString(2, userPassword);


            Boolean rs = preparedStatement.execute();
            System.out.println("Success");

        } catch (ClassNotFoundException | SQLException e) {
            System.out.println(e.getMessage());
        }
    }

}
