package controllers;

import db.DatabaseHandler;
import db.Tour;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import logger.Logger;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

import java.math.BigDecimal;

public class AddTourController {

    @FXML
    private Button addTourButton;

    @FXML
    private TextField durationTextField;

    @FXML
    private TextField fromCityTextField;

    @FXML
    private CheckBox mealsCheckBox;

    @FXML
    private TextField mealsPerDayTextField;

    @FXML
    private TextField priceTextField;

    @FXML
    private Button returnButton;

    @FXML
    private TextField toCityTextField;

    @FXML
    private ComboBox<String> transportComboBox;

    @FXML
    private TextField typeTextField;
    DatabaseHandler databaseHandler = new DatabaseHandler();

    @FXML
    void initialize() {
        transportComboBox.getItems().addAll("Літак", "Автомобіль", "Поїзд", "Автобус");

        returnButton.setOnAction(actionEvent -> {
            MainApplication.openNewScene(returnButton,"/controllers/homePage.fxml");
        });
        addTourButton.setOnAction(actionEvent -> {
            addTour();
        });
        priceTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*(\\.\\d*)?")) {
                Alert alert = new Alert(AlertType.WARNING);
                alert.setTitle("Некоректний ввід");
                alert.setHeaderText(null);
                alert.setContentText("Будь ласка введіть коректну ціну (формат 00.00 або 00).");

                alert.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        priceTextField.setText(oldValue);
                    }
                });
            }
        });
        mealsPerDayTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*") || (newValue.matches("\\d+") && Integer.parseInt(newValue) > 127)) {
                Alert alert = new Alert(AlertType.WARNING);
                alert.setTitle("Некоректний ввід");
                alert.setHeaderText(null);
                alert.setContentText("Будь ласка, введіть ціле число від 0 до 127.");

                alert.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        mealsPerDayTextField.setText(oldValue);
                    }
                });
            }
        });
        durationTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*") || (newValue.matches("\\d+") && Integer.parseInt(newValue) > 127)) {
                Alert alert = new Alert(AlertType.WARNING);
                alert.setTitle("Некоректний ввід");
                alert.setHeaderText(null);
                alert.setContentText("Будь ласка, введіть ціле число від 0 до 127.");

                alert.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        durationTextField.setText(oldValue);
                    }
                });
            }
        });
    }
    private void addTour() {
        String type = typeTextField.getText();
        String priceText = priceTextField.getText();
        String fromCity = fromCityTextField.getText();
        String toCity = toCityTextField.getText();
        String transport = transportComboBox.getSelectionModel().getSelectedItem();
        boolean meals = mealsCheckBox.isSelected();
        String mealsPerDayText = mealsPerDayTextField.getText();
        String durationText = durationTextField.getText();

        if (type.isEmpty() || priceText.isEmpty() || fromCity.isEmpty() || toCity.isEmpty() || transport == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Пусті поля");
            alert.setHeaderText(null);
            alert.setContentText("Будь ласка, заповніть всі обов'язкові поля.");
            alert.showAndWait();
            return;
        }

        BigDecimal price;
        int mealsPerDay = 0;
        int durationInDays = 0;

        try {
            price = new BigDecimal(priceText);
            if (!mealsPerDayText.isEmpty()) {
                mealsPerDay = Integer.parseInt(mealsPerDayText);
            }
            if (!durationText.isEmpty()) {
                durationInDays = Integer.parseInt(durationText);
            }
        } catch (NumberFormatException e) {
            return;
        }

        Tour tour = new Tour(0, type, price, fromCity, toCity, transport, meals, mealsPerDay, durationInDays);

        databaseHandler.addTour(tour);

        MainApplication.openNewScene(addTourButton, "/controllers/homePage.fxml");
        Logger.writeLineToFile(Logger.getCurrentTimestamp() + " New tour to " + toCityTextField.getText() + " of type " + typeTextField.getText() + " for the price of $" + priceTextField.getText() + " has been added by the user:" + MainApplication.getLoggedInUser().getUsername() + ".");
    }

}
