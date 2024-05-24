package controllers;

import db.DatabaseHandler;
import db.Tour;
import db.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(ApplicationExtension.class)
public class DeleteTourControllerTest {
    @Mock
    private TableView<Tour> toursTableView;

    private DeleteTourController controller;
    private DatabaseHandler databaseHandlerMock;
    private User fakeUser;

    @BeforeEach
    void setUp() {
        fakeUser = new User("testuser", "password");
        MainApplication.setLoggedInUser(fakeUser);
    }
    @Start
    private void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/controllers/deleteTour.fxml"));
        AnchorPane rootPane = loader.load();
        controller = loader.getController();
        databaseHandlerMock = mock(DatabaseHandler.class);
        controller.databaseHandler = databaseHandlerMock;

        Scene scene = new Scene(rootPane);
        stage.setScene(scene);
        stage.show();
    }
    // Метод для тестування кнопок та елементів сцени що змінюють сцену
    public void testSceneChange(FxRobot robot, String initialSceneElementId, String expectedEndSceneElementId) {
        // Натискання на кнопку/елемент за ідентифікатором
        robot.clickOn(initialSceneElementId);

        // Отримання поточної сцени
        Stage stage = (Stage) robot.robotContext().getWindowFinder().listWindows().stream()
                .filter(window -> window instanceof Stage)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No stage found"));

        // Отримання кореня нової сцени
        Parent root = stage.getScene().getRoot();

        // Перевірка, чи корінь нової сцени містить очікуваний елемент
        assertNotNull(root.lookup(expectedEndSceneElementId), "Expected element not found in the scene");
    }

    private void testAlertContent(FxRobot robot, String expectedContent) {
        DialogPane dialogPane = robot.lookup(".dialog-pane").queryAs(DialogPane.class);
        assertNotNull(dialogPane, "Alert dialog should be visible");

        // Перевірка вмісту алерта
        String content = dialogPane.getContentText();
        assertEquals(expectedContent, content, "Alert content should match");
        robot.clickOn("OK");
    }

    @Test
    public void testReturnButton(FxRobot robot) {
        testSceneChange(robot, "#returnButton", "#signOutButton");
    }

    @Test
    public void testHandleDeleteButtonClick(FxRobot robot) {
        // Arrange
        Tour tour = new Tour(1, "Tour Type", BigDecimal.valueOf(100), "From City", "To City", "Transport", true, 3, 7);
        ObservableList<Tour> tours = FXCollections.observableArrayList(tour);
        controller.toursTableView.setItems(tours);
        controller.toursTableView.getSelectionModel().select(0);

        when(databaseHandlerMock.loadTours()).thenReturn(FXCollections.observableArrayList());

        // Act
        robot.clickOn("#deleteButton");

        // Assert
        verify(databaseHandlerMock).deleteTour(1);
        assertNotNull(controller.toursTableView.getItems());
        assert(controller.toursTableView.getItems().isEmpty());
    }
    @Test
    public void testHandleDeleteAllButtonClickNotEmpty(FxRobot robot) {
        // Arrange
        Tour tour = new Tour(1, "Tour Type", BigDecimal.valueOf(100), "From City", "To City", "Transport", true, 3, 7);
        ObservableList<Tour> tours = FXCollections.observableArrayList(tour);
        controller.toursTableView.setItems(tours);

        // Act
        robot.clickOn("#deleteAllButton");

        // Assert
        verify(databaseHandlerMock).deleteAllTours();
        assertTrue(controller.toursTableView.getItems().isEmpty());
    }

    @Test
    public void testHandleDeleteAllButtonClickEmpty(FxRobot robot) {

        // Act
        robot.clickOn("#deleteAllButton");
        robot.clickOn("#deleteAllButton");

        // Assert
        testAlertContent(robot, "Будь ласка, додайте записи в базу даних перед видаленням.");
    }
}