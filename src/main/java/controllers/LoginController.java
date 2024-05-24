package controllers;

import animations.Shake;
import db.DatabaseHandler;
import db.PasswordHasher;
import db.User;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import logger.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginController {

    @FXML
    private Button signUpButton;

    @FXML
    private Button loginButton;

    @FXML
    private TextField loginField;

    @FXML
    private PasswordField passwordField;
    DatabaseHandler databaseHandler = new DatabaseHandler();

    @FXML
    void initialize() {
        loginButton.setOnAction(actionEvent -> {
            String loginText = loginField.getText().trim();
            String loginPassword = passwordField.getText().trim();

            if(!loginText.equals("") && !loginPassword.equals(""))
                loginUser(loginText, loginPassword);
            else{
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Логін та/або пароль пусті, введіть щось.");
                alert.showAndWait();
            }
                });
        
        // Кнопка "Зареєструватись"
        signUpButton.setOnAction(actionEvent -> {
            MainApplication.openNewScene(signUpButton,"/controllers/signUpPage.fxml");
        });
    }

    void loginUser(String loginText, String loginPassword) {
        User user = new User();
        user.setUsername(loginText);
        String hashedPassword = PasswordHasher.hashPassword(loginPassword);
        user.setPassword(hashedPassword);
        ResultSet result = databaseHandler.getUser(user);

        int counter = 0;

        try {
            while (result.next())
                counter++;
        } catch (SQLException e) {
            Logger.writeLineToFile(Logger.getCurrentTimestamp() + " Something went wrong with the login");
        }

        if (counter >= 1) {
            try {
                while (result.next()) {
                    user.setFirstName(result.getString("first_name"));
                    user.setLastName(result.getString("last_name"));
                    user.setMiddleName(result.getString("middle_name"));
                    user.setEmail(result.getString("email"));
                    user.setPhoneNumber(result.getString("phone_number"));
                    user.setUsername(result.getString("username"));
                    user.setPassword(result.getString("password"));
                    user.setGender(result.getString("gender"));
                }
            } catch (SQLException e) {
                Logger.writeLineToFile(Logger.getCurrentTimestamp() + " Something went wrong with the login");
            }

            MainApplication.setLoggedInUser(user);
            MainApplication.openNewScene(loginButton, "/controllers/homePage.fxml");
            Logger.writeLineToFile(Logger.getCurrentTimestamp() + " User " + MainApplication.getLoggedInUser().getUsername() + " has logged in.");
        } else {
            Shake userLoginAnim = new Shake(loginField);
            Shake userPasswordAnim = new Shake(passwordField);
            userLoginAnim.playAnim();
            userPasswordAnim.playAnim();
        }
    }
}
