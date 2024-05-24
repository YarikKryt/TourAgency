package controllers;

import db.DatabaseHandler;
import db.PasswordHasher;
import db.User;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.DialogPane;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import javax.resource.cci.ResultSet;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(ApplicationExtension.class)
class LoginControllerTest {
    private LoginController controller;
    private DatabaseHandler databaseHandlerMock;
    private User fakeUser;

    @BeforeEach
    void setUp() {
        fakeUser = new User("testuser", "password");
        MainApplication.setLoggedInUser(fakeUser);
    }
    @Start
    private void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/controllers/loginPage.fxml"));
        AnchorPane rootPane = loader.load();
        controller = loader.getController();
        databaseHandlerMock = mock(DatabaseHandler.class);
        controller.databaseHandler = databaseHandlerMock;

        Scene scene = new Scene(rootPane);
        stage.setScene(scene);
        stage.show();
    }
    private void testAlertContent(FxRobot robot, String expectedContent) {
        DialogPane dialogPane = robot.lookup(".dialog-pane").queryAs(DialogPane.class);
        assertNotNull(dialogPane, "Alert dialog should be visible");

        // Перевірка вмісту алерта
        String content = dialogPane.getContentText();
        assertEquals(expectedContent, content, "Alert content should match");
        robot.clickOn("OK");
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

    private ResultSet createResultSetForUser(User user, String hashedPassword) {
        // Створення мокового ResultSet для користувача
        ResultSet resultSetMock = mock(ResultSet.class);
        try {
            when(resultSetMock.next()).thenReturn(true, false); // Імітуємо одну строку у ResultSet
            when(resultSetMock.getString("first_name")).thenReturn(user.getFirstName());
            when(resultSetMock.getString("last_name")).thenReturn(user.getLastName());
            when(resultSetMock.getString("middle_name")).thenReturn(user.getMiddleName());
            when(resultSetMock.getString("email")).thenReturn(user.getEmail());
            when(resultSetMock.getString("phone_number")).thenReturn(user.getPhoneNumber());
            when(resultSetMock.getString("username")).thenReturn(user.getUsername());
            when(resultSetMock.getString("password")).thenReturn(hashedPassword);
            when(resultSetMock.getString("gender")).thenReturn(user.getGender());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return resultSetMock;
    }

    @Test
    public void testReturnButton(FxRobot robot) {
        testSceneChange(robot, "#signUpButton", "#signUpFinishButton");
    }

    @Test
    public void testLoginUserWithCorrectCredentials(FxRobot robot) {
        // Arrange
        MainApplication.setLoggedInUser(null);

        // Створення користувача "test"
        User testUser = new User("Test", "User", "Test", "test@example.com", "333333333", "test", "Testpassword1234!", "Чоловік");
        String hashedPassword = PasswordHasher.hashPassword("Testpassword1234!");

        // Налаштування поведінки мока
        ResultSet mockResultSet = createResultSetForUser(testUser, hashedPassword);
        when(databaseHandlerMock.getUser(any(User.class))).thenReturn(mockResultSet);

        // Act
        robot.clickOn("#loginField");
        robot.write("test");
        robot.clickOn("#passwordField");
        robot.write("Testpassword1234!");
        robot.clickOn("#loginButton");

        // Assert
        assertNotNull(MainApplication.getLoggedInUser(), "User should be logged in with correct credentials");
        assertEquals(testUser.getUsername(), MainApplication.getLoggedInUser().getUsername());
    }


    @Test
    public void testLoginUserWithIncorrectCredentials(FxRobot robot) {
        // Arrange
        MainApplication.setLoggedInUser(null);
        // Створення мок-об'єкта ResultSet
        ResultSet mockResultSet = Mockito.mock(ResultSet.class);
        // Налаштування поведінки мок-об'єкта databaseHandlerMock
        Mockito.when(databaseHandlerMock.getUser(Mockito.any(User.class))).thenReturn(mockResultSet);

        // Act
        robot.clickOn("#loginField");
        robot.write("incorrectuser");
        robot.clickOn("#passwordField");
        robot.write("incorrectpassword");
        robot.clickOn("#loginButton");

        // Assert
        assertNull(MainApplication.getLoggedInUser(), "User should not be logged in with incorrect credentials");
    }
    @Test
    public void testLoginUserWithBlankCredentials(FxRobot robot) {
        // Act
        robot.clickOn("#loginButton");

        testAlertContent(robot, "Логін та/або пароль пусті, введіть щось.");
    }
}