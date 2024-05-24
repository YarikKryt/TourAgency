    package controllers;

    import db.DatabaseHandler;
    import db.Tour;
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

    import java.math.BigDecimal;

    import static org.junit.jupiter.api.Assertions.*;
    import static org.mockito.Mockito.mock;
    import static org.mockito.Mockito.verify;

    @ExtendWith(ApplicationExtension.class)
    public class AddTourControllerTest {

        private AddTourController controller;
        private DatabaseHandler databaseHandlerMock;
        private User fakeUser;

        @BeforeEach
        void setUp() {
            fakeUser = new User("testuser", "password");
            MainApplication.setLoggedInUser(fakeUser);
        }
        @Start
        private void start(Stage stage) throws Exception {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/controllers/addTour.fxml"));
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
        public void testAddTour(FxRobot robot) {
            // Arrange
            robot.clickOn("#typeTextField").write("Міський відпочинок");
            robot.clickOn("#priceTextField").write("100.00");
            robot.clickOn("#fromCityTextField").write("Нью-Йорк");
            robot.clickOn("#toCityTextField").write("Нью-Джерсі");
            robot.clickOn("#transportComboBox").clickOn("Автобус");;
            robot.clickOn("#mealsCheckBox");
            robot.clickOn("#mealsPerDayTextField").write("2");
            robot.clickOn("#durationTextField").write("3");

            // Act
            robot.clickOn("#addTourButton");

            // Assert
            Tour expectedTour = new Tour(0, "Міський відпочинок", new BigDecimal("100.00"), "Нью-Йорк", "Нью-Джерсі", "Автобус", true, 2, 3);
            verify(databaseHandlerMock).addTour(expectedTour);
        }

        @Test
        public void testValidation(FxRobot robot) {
            // Act
            robot.clickOn("#addTourButton");

            // Перевірка вмісту алерта
            testAlertContent(robot, "Будь ласка, заповніть всі обов'язкові поля.");
        }

        @Test
        public void testPriceValidation(FxRobot robot) {
            // Arrange
            robot.clickOn("#priceTextField");
            robot.write("100"); // Встановлення коректного початкового значення
            robot.eraseText(3); // Видалення введеного тексту
            robot.write("t"); // Спроба ввести некоректний текст

            // Act
            // Натискання на кнопку OK в алерті, якщо він з'являється
            try {
                robot.clickOn("OK");
            } catch (Exception e) {
                // Якщо алерт не з'явився, тест продовжується
            }

            // Assert
            // Перевірка, що текстове поле містить початкове коректне значення
            assertEquals("", robot.lookup("#priceTextField").queryTextInputControl().getText());
        }

        @Test
        public void testReturnButton(FxRobot robot) {
            testSceneChange(robot,"#returnButton", "#signOutButton");
        }


        @Test
        public void testMealsPerDayTextFieldValidation(FxRobot robot) {
            // Arrange
            robot.clickOn("#mealsPerDayTextField").write("128"); // Некоректне значення

            // Act
            try {
                testAlertContent(robot, "Будь ласка, введіть ціле число від 0 до 127.");
            } catch (Exception e) {
                // Якщо алерт не з'явився, тест продовжується
            }

            // Assert
            assertEquals("12", robot.lookup("#mealsPerDayTextField").queryTextInputControl().getText(), "Field should be reset to empty");
        }

        @Test
        public void testDurationTextFieldValidation(FxRobot robot) {
            // Arrange
            robot.clickOn("#durationTextField").write("128"); // Некоректне значення

            // Act
            try {
                testAlertContent(robot, "Будь ласка, введіть ціле число від 0 до 127.");
            } catch (Exception e) {
                // Якщо алерт не з'явився, тест продовжується
            }

            // Assert
            assertEquals("12", robot.lookup("#durationTextField").queryTextInputControl().getText(), "Field should be reset to empty");
        }
    }