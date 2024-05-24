package controllers;

import db.DatabaseHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import logger.Logger;

public class HomePageController {

    @FXML
    private Button addTourButton;

    @FXML
    private Button deleteTourButton;

    @FXML
    private Button signOutButton;

    @FXML
    private Button viewToursButton;
    DatabaseHandler databaseHandler = new DatabaseHandler();

    @FXML
    void initialize() {
        signOutButton.setOnAction(actionEvent -> {
            MainApplication.openNewScene(signOutButton,"/controllers/loginPage.fxml");
            Logger.writeLineToFile(Logger.getCurrentTimestamp() + " User " + MainApplication.getLoggedInUser().getUsername() + " has signed out.");
        });
        viewToursButton.setOnAction(actionEvent -> {
            MainApplication.openNewScene(viewToursButton,"/controllers/viewTours.fxml");
        });
        addTourButton.setOnAction(actionEvent -> {
            MainApplication.openNewScene(addTourButton,"/controllers/addTour.fxml");
        });
        deleteTourButton.setOnAction(actionEvent -> {
            MainApplication.openNewScene(deleteTourButton,"/controllers/deleteTour.fxml");
        });
    }
}
