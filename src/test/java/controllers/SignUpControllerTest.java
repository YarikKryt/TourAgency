package controllers;

import db.DatabaseHandler;
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
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

@ExtendWith(ApplicationExtension.class)
class SignUpControllerTest {
    private SignUpController controller;
    private DatabaseHandler databaseHandlerMock;
    private User fakeUser;

    @BeforeEach
    void setUp() {
        fakeUser = new User("testuser", "password");
        MainApplication.setLoggedInUser(fakeUser);
    }

    @Start
    private void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/controllers/signUpPage.fxml"));
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
        testSceneChange(robot, "#signUpReturnButton", "#signUpButton");
    }

    @Test
    public void testSignUpUser(FxRobot robot) {
        MainApplication.setLoggedInUser(null);
        // Arrange
        robot.clickOn("#signUpName").write("Test");
        robot.clickOn("#signUpLastName").write("Test");
        robot.clickOn("#signUpMiddleName").write("Test");
        robot.clickOn("#signUpEmail").write("Test@test.com");
        robot.clickOn("#signUpPhoneNumber").write("333333333");
        robot.clickOn("#signUpLogin").write("Test");
        robot.clickOn("#signUpPassword").write("Testpassword1234!");
        robot.clickOn("#signUpCheckBoxMale");

        // Act
        robot.clickOn("#signUpFinishButton");

        // Assert
        User expectedUser = new User("Test", "Test", "Test", "Test@test.com", "+380333333333", "Test", "Testpassword1234!", "Чоловік");

        // Отримання зареєстрованого користувача
        User actualUser = MainApplication.getLoggedInUser();
        assertNotNull(actualUser, "Logged in user should not be null");
        assertEquals(expectedUser.getFirstName(), actualUser.getFirstName(), "First name should match");
        assertEquals(expectedUser.getLastName(), actualUser.getLastName(), "Last name should match");
        assertEquals(expectedUser.getMiddleName(), actualUser.getMiddleName(), "Middle name should match");
        assertEquals(expectedUser.getEmail(), actualUser.getEmail(), "Email should match");
        assertEquals(expectedUser.getPhoneNumber(), actualUser.getPhoneNumber(), "Phone number should match");
        assertEquals(expectedUser.getUsername(), actualUser.getUsername(), "Username should match");
        assertEquals(expectedUser.getPassword(), actualUser.getPassword(), "Password should match");
        assertEquals(expectedUser.getGender(), actualUser.getGender(), "Gender should match");
    }

    @Test
    public void testInvalidEmailAlert(FxRobot robot) {
        robot.clickOn("#signUpEmail").write("invalidemail");
        robot.clickOn("#signUpLastName"); // Втратити фокус для виклику перевірки

        // Перевірка, чи відображається Alert з правильним текстом
        testAlertContent(robot, "Будь ласка, введіть дійсну адресу електронної пошти.");
    }

    @Test
    public void testInvalidSignUpPasswordAlert(FxRobot robot) {
        robot.clickOn("#signUpPassword").write("invalidpassword");
        robot.clickOn("#signUpLogin"); // Втратити фокус для виклику перевірки

        // Перевірка, чи відображається Alert з правильним текстом
        testAlertContent(robot, "Будь ласка, введіть коректний пароль. Ваш пароль повинен містити щонайменше 8 символів, включаючи одну велику літеру, одну малу літеру, одну цифру та один спеціальний символ.");
    }

    @Test
    public void blankFieldsAlert(FxRobot robot) {
        robot.clickOn("#signUpEmail");
        robot.clickOn("#signUpFinishButton");

        // Перевірка, чи відображається Alert з правильним текстом
        testAlertContent(robot, "Будь ласка, заповніть всі обов'язкові поля.");
    }

    @Test
    public void testInvalidLoginAlert(FxRobot robot) {
        robot.clickOn("#signUpLogin").write("1234567890123456789012345678901");
        robot.clickOn("#signUpPassword"); // Trigger focus lost event

        testAlertContent(robot, "Будь ласка, введіть коректний логін (дозволені лише літери, цифри, підкреслення та дефіс) до 30 символів.");
    }

    @Test
    public void testLoginUserWithCorrectCredentials(FxRobot robot) {
        MainApplication.setLoggedInUser(null);
        User testUser = new User("Test", "User", "Test", "test@example.com", "+380333333333", "test", "Testpassword1234!", "Чоловік");

        // Налаштування mock об'єкта, щоб він кидав виняток при виклику signUpUser
        doThrow(new RuntimeException("Username, email, or phone number is already taken")).when(databaseHandlerMock).signUpUser(any(User.class));

        // Arrange
        robot.clickOn("#signUpName").write("Test");
        robot.clickOn("#signUpLastName").write("Test");
        robot.clickOn("#signUpMiddleName").write("Test");
        robot.clickOn("#signUpEmail").write("test@example.com");
        robot.clickOn("#signUpPhoneNumber").write("333333333");
        robot.clickOn("#signUpLogin").write("test");
        robot.clickOn("#signUpPassword").write("Testpassword1234!");
        robot.clickOn("#signUpCheckBoxMale");

        // Act
        robot.clickOn("#signUpFinishButton");

        // Перевірка, чи відображається Alert з правильним текстом
        testAlertContent(robot, "Ім'я користувача, електронна пошта або номер телефону вже зайняті.");
    }

    @Test
    public void notFullSignUpPhoneNumberAlert(FxRobot robot) {
        robot.clickOn("#signUpPhoneNumber").write("333");
        robot.clickOn("#signUpLogin"); // Втратити фокус для виклику перевірки

        // Перевірка, чи відображається Alert з правильним текстом
        testAlertContent(robot, "Будь ласка, введіть повний номер телефону.");
    }
}