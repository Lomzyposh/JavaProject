package LandingPage.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.ImageView;
import javafx.scene.text.Font;

import java.io.IOException;

public class landingPage {

    public void goToLoginPage(ActionEvent event) throws IOException {
        Parent loginRoot = FXMLLoader.load(getClass().getResource("/LoginPage/view/login.fxml"));
        Scene scene = ((Node) event.getSource()).getScene();
        scene.setRoot(loginRoot);
    }

    public void initialize() {
    }

}
