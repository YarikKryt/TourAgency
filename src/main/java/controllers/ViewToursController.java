package controllers;

import db.DatabaseHandler;
import db.Tour;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.util.List;

import javafx.scene.control.Alert.AlertType;

public class ViewToursController {

    @FXML
    private TableColumn<?, ?> mealsPerDayTableColumn;

    @FXML
    private TableColumn<?, ?> durationInDaysTableColumn;

    @FXML
    private TableColumn<?, ?> mealsTableColumn;

    @FXML
    private TableColumn<?, ?> fromCityTableColumn;

    @FXML
    private TableColumn<?, ?> priceTableColumn;

    @FXML
    private Button returnButton;

    @FXML
    private TableColumn<?, ?> toCityTableColumn;

    @FXML
    private TableColumn<?, ?> tourIDTableColumn;

    @FXML
    private TableColumn<?, ?> typeTableColumn;

    @FXML
    TableView<Tour> toursTableView;

    @FXML
    private TableColumn<?, ?> transportTableColumn;

    @FXML
    ComboBox<String> transportComboBox;

    @FXML
    CheckBox mealsCheckBox;

    @FXML
    TextField durationTextField;

    @FXML
    private Button applyFilterButton;

    @FXML
    private Button cancelSearchButton;
    DatabaseHandler databaseHandler = new DatabaseHandler();

    @FXML
    void initialize() {
        returnButton.setOnAction(actionEvent -> {
            MainApplication.openNewScene(returnButton, "/controllers/homePage.fxml");
        });

        // Завантаження турів з бази даних
        List<Tour> tours = loadToursFromDatabase();

        // Встановлення елементів для відображення в таблиці
        toursTableView.setItems(FXCollections.observableArrayList(tours));

        // Встановлення фабрик значень для кожної колонки таблиці
        tourIDTableColumn.setCellValueFactory(new PropertyValueFactory<>("tourID"));
        typeTableColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        priceTableColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        fromCityTableColumn.setCellValueFactory(new PropertyValueFactory<>("fromCity"));
        toCityTableColumn.setCellValueFactory(new PropertyValueFactory<>("toCity"));
        transportTableColumn.setCellValueFactory(new PropertyValueFactory<>("transport"));
        mealsTableColumn.setCellValueFactory(new PropertyValueFactory<>("meals"));
        mealsPerDayTableColumn.setCellValueFactory(new PropertyValueFactory<>("mealsPerDay"));
        durationInDaysTableColumn.setCellValueFactory(new PropertyValueFactory<>("durationInDays"));

        // Ініціалізація фільтрів
        FilteredList<Tour> filteredData = new FilteredList<>(toursTableView.getItems(), p -> true);
        toursTableView.setItems(filteredData);

        transportComboBox.getItems().addAll("Літак", "Автомобіль", "Поїзд", "Автобус");
        transportComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            applyFilters(filteredData);
        });

        mealsCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            applyFilters(filteredData);
        });

        durationTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            applyFilters(filteredData);
        });

        applyFilterButton.setOnAction(event -> {
            applyFilters(filteredData);
        });

        cancelSearchButton.setOnAction(event -> {
            transportComboBox.getSelectionModel().clearSelection();
            mealsCheckBox.setSelected(false);
            durationTextField.clear();

            // Скидання фільтрованих даних до початкових даних
            filteredData.setPredicate(tour -> true);
        });

        durationTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                Alert alert = new Alert(AlertType.WARNING);
                alert.setTitle("Invalid Input");
                alert.setHeaderText(null);
                alert.setContentText("Будь ласка, введіть правильний номер.");

                alert.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        durationTextField.setText(oldValue);
                    }
                });
            } else {
                applyFilters(filteredData);
            }
        });
    }

    // Завантаження турів з бази даних
    List<Tour> loadToursFromDatabase() {
        DatabaseHandler databaseHandler = new DatabaseHandler();
        return databaseHandler.loadTours();
    }

    void applyFilters(FilteredList<Tour> filteredData) {
        String selectedTransport = transportComboBox.getSelectionModel().getSelectedItem();
        boolean selectedMeals = mealsCheckBox.isSelected() || mealsCheckBox.isIndeterminate();
        String durationText = durationTextField.getText();
        int duration = 0;
        if (!durationText.isEmpty()) {
            try {
                duration = Integer.parseInt(durationText);
            } catch (NumberFormatException e) {
            }
        }

        int finalDuration = duration;
        filteredData.setPredicate(tour -> {
            if (selectedTransport != null && !selectedTransport.equals(tour.getTransport())) {
                return false;
            }
            if ((selectedMeals && !tour.getMeals()) || (!selectedMeals && tour.getMeals())) {
                return false;
            }
            if (finalDuration != 0 && finalDuration != tour.getDurationInDays()) {
                return false;
            }
            return true;
        });
    }
}

