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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class signUpController implements Initializable {

    @FXML
    public VBox signUpForm;
    @FXML
    public AnchorPane rootPane;
    @FXML
    public Button fadeTop;

    public void goToLogin(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/LoginPage/view/login.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
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
}
