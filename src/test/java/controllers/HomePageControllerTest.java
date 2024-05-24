package controllers;

import db.DatabaseHandler;
import db.User;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

@ExtendWith(ApplicationExtension.class)
class HomePageControllerTest {
    private HomePageController controller;
    private DatabaseHandler databaseHandlerMock;
    private User fakeUser;

    @BeforeEach
    void setUp() {
        fakeUser = new User("testuser", "password");
        MainApplication.setLoggedInUser(fakeUser);
    }
    @Start
    private void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/controllers/homePage.fxml"));
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

    @Test
    public void testSignOutButton(FxRobot robot) {
        testSceneChange(robot, "#signOutButton", "#loginButton");
    }
    @Test
    public void testViewToursButton(FxRobot robot) {
        testSceneChange(robot, "#viewToursButton", "#applyFilterButton");
    }
    @Test
    public void testAddTourButton(FxRobot robot) {
        testSceneChange(robot, "#addTourButton", "#mealsCheckBox");
    }
    @Test
    public void testDeleteTourButton(FxRobot robot) {
        testSceneChange(robot, "#deleteTourButton", "#deleteButton");
    }
}