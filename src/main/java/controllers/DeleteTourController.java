package controllers;

import db.DatabaseHandler;
import db.Tour;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import logger.Logger;

import java.util.List;

public class DeleteTourController {
    @FXML
    private Button deleteAllButton;

    @FXML
    private Button deleteButton;

    @FXML
    private TableColumn<?, ?> durationInDaysTableColumn;

    @FXML
    private TableColumn<?, ?> fromCityTableColumn;

    @FXML
    private TableColumn<?, ?> mealsPerDayTableColumn;

    @FXML
    private TableColumn<?, ?> mealsTableColumn;

    @FXML
    private TableColumn<?, ?> priceTableColumn;

    @FXML
    private Button returnButton;

    @FXML
    private TableColumn<?, ?> toCityTableColumn;

    @FXML
    private TableColumn<?, ?> tourIDTableColumn;

    @FXML
    TableView<Tour> toursTableView;

    @FXML
    private TableColumn<?, ?> transportTableColumn;

    @FXML
    private TableColumn<?, ?> typeTableColumn;
    DatabaseHandler databaseHandler = new DatabaseHandler();

    @FXML
    void initialize() {
        returnButton.setOnAction(actionEvent -> {
            MainApplication.openNewScene(returnButton,"/controllers/homePage.fxml");
        });
        deleteButton.setOnAction(actionEvent -> {
            handleDeleteButtonClick();
        });
        deleteAllButton.setOnAction(actionEvent -> {
            handleDeleteAllButtonClick();
        });
        // Load tours from the database
        List<Tour> tours = loadToursFromDatabase();

        // Set the items of the table view
        toursTableView.setItems(FXCollections.observableArrayList(tours));

        // Set cell value factories for each table column
        tourIDTableColumn.setCellValueFactory(new PropertyValueFactory<>("tourID"));
        typeTableColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        priceTableColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        fromCityTableColumn.setCellValueFactory(new PropertyValueFactory<>("fromCity"));
        toCityTableColumn.setCellValueFactory(new PropertyValueFactory<>("toCity"));
        transportTableColumn.setCellValueFactory(new PropertyValueFactory<>("transport"));
        mealsTableColumn.setCellValueFactory(new PropertyValueFactory<>("meals"));
        mealsPerDayTableColumn.setCellValueFactory(new PropertyValueFactory<>("mealsPerDay"));
        durationInDaysTableColumn.setCellValueFactory(new PropertyValueFactory<>("durationInDays"));
    }
    private List<Tour> loadToursFromDatabase() {
        return databaseHandler.loadTours();
    }
    @FXML
    void handleDeleteButtonClick() {
        Tour selectedTour = toursTableView.getSelectionModel().getSelectedItem();
        if (selectedTour != null) {
            databaseHandler.deleteTour(selectedTour.getTourID());
            // Update the table view with the new data
            toursTableView.setItems(FXCollections.observableArrayList(databaseHandler.loadTours()));
            Logger.writeLineToFile(Logger.getCurrentTimestamp() + " The tour to " + selectedTour.getToCity() + " with ID:" + selectedTour.getTourID() + ", cost:$" + selectedTour.getPrice() + ", and type:" + selectedTour.getType() + " has been deleted by the user:" + MainApplication.getLoggedInUser().getUsername() + ".");
        }
    }
    @FXML
    void handleDeleteAllButtonClick() {
        if (!toursTableView.getItems().isEmpty()) {
            databaseHandler.deleteAllTours();
            // Оновлення таблиці з новими даними
            toursTableView.setItems(FXCollections.observableArrayList(databaseHandler.loadTours()));
            Logger.writeLineToFile(Logger.getCurrentTimestamp() + " The DB counter was reset and all tours have been deleted by the user:" + MainApplication.getLoggedInUser().getUsername() + ".");
        }else{
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("База даних пуста");
        alert.setHeaderText(null);
        alert.setContentText("Будь ласка, додайте записи в базу даних перед видаленням.");
        alert.showAndWait();
        return;
        }
    }
}
