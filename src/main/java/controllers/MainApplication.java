package controllers;

import db.User;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import java.io.IOException;
import java.util.Objects;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("loginPage.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 700, 400);
        stage.setTitle("Tour Agency");
        String iconLink = Objects.requireNonNull(getClass().getResource("/assets/TourAgencyIcon.png")).toExternalForm();
        Image icon = new Image(iconLink);
        stage.getIcons().add(icon);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    public static void openNewScene(Button button, String window) {
        try {
            FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource(window));
            Parent root = loader.load();
            Stage stage = (Stage) button.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setResizable(false);
        }
        catch (IllegalStateException | IOException e) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Помилка загрузки вікна");
            alert.setHeaderText(null);
            alert.setContentText("Помилка загрузки нового вікна.");
            alert.close();
        }
    }

    private static User loggedInUser;
    public static void setLoggedInUser(User user) {
        loggedInUser = user;
    }

    public static User getLoggedInUser() {
        return loggedInUser;
    }
}