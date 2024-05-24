package controllers;

import db.DatabaseHandler;
import db.User;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import logger.Logger;
import javafx.scene.control.Alert;
import javafx.scene.control.TextFormatter;
import java.util.function.UnaryOperator;

public class SignUpController {

    @FXML
    private Button signUpFinishButton;

    @FXML
    private CheckBox signUpCheckBoxFemale;

    @FXML
    private CheckBox signUpCheckBoxMale;

    @FXML
    private TextField signUpEmail;

    @FXML
    private TextField signUpLastName;

    @FXML
    private TextField signUpLogin;

    @FXML
    private TextField signUpMiddleName;

    @FXML
    private PasswordField signUpPassword;

    @FXML
    private TextField signUpPhoneNumber;

    @FXML
    private Button signUpReturnButton;

    @FXML
    private TextField signUpName;
    DatabaseHandler databaseHandler = new DatabaseHandler();

    @FXML
    void initialize() {
        // Ініціалізуємо фільтр для валідації поля вводу email
        UnaryOperator<TextFormatter.Change> filter = change -> change; // Дозволяємо введення будь-якого тексту

        // Додаємо обробник подій для втрати поля вводу фокусу
        signUpEmail.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) { // Перевіряємо, чи поле вводу втратило фокус
                String text = signUpEmail.getText();
                // Перевіряємо, чи поле не пусте і чи введений текст є коректним імейлом
                if (!text.isEmpty() && (!text.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$") || !text.contains("@"))) {
                    // Відображаємо повідомлення про помилку
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Неправильна адреса електронної пошти");
                    alert.setHeaderText(null);
                    alert.setContentText("Будь ласка, введіть дійсну адресу електронної пошти.");
                    alert.showAndWait();
                    // Очищаємо поле вводу
                    signUpEmail.clear();
                }
            }
        });

        // Встановлюємо TextFormatter для поля вводу
        signUpEmail.setTextFormatter(new TextFormatter<>(filter));

        // Робимо логіку для поля вводу номеру телефону (обмеження у довжині та символах) - латинські символи, загальна кількість символів: 13
        // Додаємо обробник подій для втрати поля вводу фокусу
        signUpPhoneNumber.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) { // Перевіряємо, чи поле вводу втратило фокус
                String text = signUpPhoneNumber.getText();
                if (!text.matches("^\\+380") && text.length() < 13) {
                    // Відображаємо повідомлення про помилку
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Неправильний номер телефону");
                    alert.setHeaderText(null);
                    alert.setContentText("Будь ласка, введіть повний номер телефону.");
                    alert.showAndWait();

                    // Очищаємо поле вводу
                    signUpPhoneNumber.clear();
                    signUpPhoneNumber.setText("+380");
                }
            }
        });

        // Встановлюємо TextFormatter для поля вводу
        signUpEmail.setTextFormatter(new TextFormatter<>(filter));

        // Робимо логіку для поля вводу номеру телефону (обмеження у довжині та символах) - латинські символи, загальна кількість символів: 13
        signUpPhoneNumber.setText("+380");

        filter = change -> {
            String text = change.getControlNewText();
            if (text.startsWith("+380") && text.length() <= 13 && text.substring(4).matches("\\d*")) {
                return change;
            } else {
                return null;
            }
        };

        signUpPhoneNumber.setTextFormatter(new TextFormatter<String>(filter));

        // Робимо логіку для поля вводу логіну
        UnaryOperator<TextFormatter.Change> loginFilter = change -> {
            String text = change.getControlNewText();
            // Перевіряємо, чи введений текст містить лише латинські букви та деякі спеціальні символи
            if (text.matches("^[a-zA-Z0-9_-]*$")) {
                return change; // Повертаємо зміни, якщо логін коректний
            } else {
                return null; // Відкидаємо зміни, якщо логін некоректний
            }
        };

        // Додаємо обробник подій для втрати поля вводу фокусу
        signUpLogin.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) { // Перевіряємо, чи поле вводу втратило фокус
                String text = signUpLogin.getText();
                // Перевіряємо, чи введений текст містить лише латинські букви та деякі спеціальні символи
                if (!text.matches("^[a-zA-Z0-9_-]{0,30}$")) {
                    // Відображаємо повідомлення про помилку
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Некоректний логін");
                    alert.setHeaderText(null);
                    alert.setContentText("Будь ласка, введіть коректний логін (дозволені лише літери, цифри, підкреслення та дефіс) до 30 символів.");
                    alert.showAndWait();
                    signUpLogin.clear();
                }
            }
        });

        // Встановлюємо TextFormatter для поля вводу логіну
        signUpLogin.setTextFormatter(new TextFormatter<>(loginFilter));

        // Робимо логіку для поля вводу пароля
        UnaryOperator<TextFormatter.Change> passwordFilter = change -> change; // Дозволяємо введення будь-якого тексту

        // Додаємо обробник подій для втрати поля вводу фокусу
        signUpPassword.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) { // Перевіряємо, чи поле вводу втратило фокус
                String text = signUpPassword.getText().trim(); // Обрізаємо пробіли
                // Перевіряємо, чи поле не пусте
                if (!text.isEmpty()) {
                    // Перевіряємо, чи введений текст відповідає стандартним вимогам до пароля
                    if (!text.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).{8,}$")) {
                        // Відображаємо повідомлення про помилку
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("Некоректний пароль");
                        alert.setHeaderText(null);
                        alert.setContentText("Будь ласка, введіть коректний пароль. Ваш пароль повинен містити щонайменше 8 символів, включаючи одну велику літеру, одну малу літеру, одну цифру та один спеціальний символ.");
                        alert.showAndWait();
                        // Очищаємо поле вводу
                        signUpPassword.clear();
                    }
                }
            }
        });

        // Встановлюємо TextFormatter для поля вводу пароля
        signUpPassword.setTextFormatter(new TextFormatter<>(passwordFilter));

        // Робимо логіку щоб тільки один чекбокс працював, та інший автоматично вимикався якщо він вибраний
        signUpCheckBoxMale.setOnAction(event -> {
            if (signUpCheckBoxMale.isSelected()) {
                signUpCheckBoxFemale.setSelected(false);
            }
        });

        signUpCheckBoxFemale.setOnAction(event -> {
            if (signUpCheckBoxFemale.isSelected()) {
                signUpCheckBoxMale.setSelected(false);
            }
        });

        signUpReturnButton.setOnAction(actionEvent -> {
            MainApplication.openNewScene(signUpReturnButton,"/controllers/loginPage.fxml");
        });

        signUpFinishButton.setOnAction(actionEvent -> {
            signUpNewUser();
        });

    }

    // Метод для реєстрації користувачів
    private void signUpNewUser() {
        DatabaseHandler dbHandler = databaseHandler;

        String firstName = signUpName.getText();
        String lastName = signUpLastName.getText();
        String MiddleName = signUpMiddleName.getText();
        String Email = signUpEmail.getText();
        String PhoneNumber = signUpPhoneNumber.getText();
        String Username = signUpLogin.getText();
        String Password = signUpPassword.getText();
        String Gender = signUpCheckBoxMale.isSelected() ? "Чоловік" : "Жіночий";

        if (firstName.isEmpty() || lastName.isEmpty() || MiddleName.isEmpty() || Email.isEmpty() || PhoneNumber.isEmpty() || PhoneNumber.matches("^\\+380") || Username.isEmpty() || Password.isEmpty() || !signUpCheckBoxMale.isSelected() && !signUpCheckBoxFemale.isSelected()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Пусті поля");
            alert.setHeaderText(null);
            alert.setContentText("Будь ласка, заповніть всі обов'язкові поля.");
            alert.showAndWait();
            return; // Зупиняємо метод, якщо будь яке з полей пусте
        } else {
            try {
                User user = new User(firstName, lastName, MiddleName, Email, PhoneNumber, Username, Password, Gender);

                dbHandler.signUpUser(user);
                MainApplication.setLoggedInUser(user);
                MainApplication.openNewScene(signUpReturnButton,"/controllers/homePage.fxml");
                Logger.writeLineToFile(Logger.getCurrentTimestamp() + " Username: " + signUpLogin.getText() + " has successfully signed up.");
            } catch (RuntimeException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Ім'я користувача, електронна пошта або номер телефону вже зайняті.");
                alert.showAndWait();
            }
        }
    }
}
