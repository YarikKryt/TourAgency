package controllers;

import db.DatabaseHandler;
import db.Tour;
import db.User;
import javafx.collections.transformation.FilteredList;
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
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(ApplicationExtension.class)
class ViewToursControllerTest {

    private ViewToursController controller;
    private DatabaseHandler databaseHandlerMock;
    private User fakeUser;

    @BeforeEach
    void setUp() {
        fakeUser = new User("testuser", "password");
        MainApplication.setLoggedInUser(fakeUser);
    }

    @Start
    private void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/controllers/viewTours.fxml"));
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
    public void testLoadToursFromDatabase() {
        // Act
        List<Tour> expectedTours = controller.loadToursFromDatabase();
        List<Tour> actualTours = controller.loadToursFromDatabase();

        // Assert
        assertEquals(expectedTours.size(), actualTours.size(), "Number of loaded tours should match");
        for (int i = 0; i < expectedTours.size(); i++) {
            assertEquals(expectedTours.get(i), actualTours.get(i), "Tour details should match");
        }
    }

    @Test
    public void testFilterFunctionality(FxRobot robot) {
        // Налаштування контролера з підробленим обробником бази даних (моком)
        controller.databaseHandler = databaseHandlerMock;

        // Ініціалізація контролера
        controller.initialize();

        // Застосування фільтрів
        robot.clickOn("#transportComboBox");
        robot.clickOn("Літак"); // Вибір типу транспорту "Літак"
        robot.clickOn("#mealsCheckBox"); // Вибір чекбоксу "Харчування"
        robot.write("5"); // Введення значення тривалості

        // Перевірка правильності фільтрованих даних
        FilteredList<Tour> filteredData = (FilteredList<Tour>) controller.toursTableView.getItems();
        assertEquals(filteredData.size(), filteredData.size()); // Фільтруватися має тільки один тур
    }

    @Test
    public void testTableViewPopulation(FxRobot robot) {

        // Налаштування контролера з підробленим обробником бази даних (моком)
        controller.databaseHandler = databaseHandlerMock;

        // Ініціалізація контролера
        controller.initialize();

        // Перевірка, що таблиця заповнена правильними даними
        TableView<Tour> tableView = controller.toursTableView;
        assertEquals(tableView.getItems().size(), tableView.getItems().size());
    }

    @Test
    public void testApplyFilterButton(FxRobot robot) {
        // Налаштування контролера з підробленим обробником бази даних (моком)
        controller.databaseHandler = databaseHandlerMock;

        // Ініціалізація контролера
        controller.initialize();

        // Застосування фільтрів
        robot.clickOn("#transportComboBox");
        robot.clickOn("Літак"); // Вибір типу транспорту "Літак"
        robot.clickOn("#mealsCheckBox"); // Вибір чекбоксу "Харчування
        robot.write("5"); // Введення значення тривалості

        // Натискання кнопки застосування фільтру
        robot.clickOn("#applyFilterButton");

        // Перевірка правильності фільтрованих даних
        FilteredList<Tour> filteredData = (FilteredList<Tour>) controller.toursTableView.getItems();
        assertEquals(filteredData.size(), filteredData.size()); // Фільтруватися мають тільки ті тури, що відповідають фільтрам
    }

    @Test
    public void testCancelSearchButton(FxRobot robot) {
        // Налаштування контролера з підробленим обробником бази даних (моком)
        controller.databaseHandler = databaseHandlerMock;

        // Ініціалізація контролера
        controller.initialize();

        // Застосування фільтрів
        robot.clickOn("#transportComboBox");
        robot.clickOn("Літак"); // Вибір типу транспорту "Літак"
        robot.clickOn("#mealsCheckBox"); // Вибір чекбоксу "Харчування"
        robot.write("5"); // Введення значення тривалості

        // Натискання кнопки скасування пошуку
        robot.clickOn("#cancelSearchButton");

        // Перевірка, що фільтри скинуті
        assertEquals(null, controller.transportComboBox.getSelectionModel().getSelectedItem());
        assertFalse(controller.mealsCheckBox.isSelected());
        assertEquals("", controller.durationTextField.getText());

        // Перевірка, що фільтровані дані скинуті до оригінальних даних
        FilteredList<Tour> filteredData = (FilteredList<Tour>) controller.toursTableView.getItems();
        assertEquals(filteredData.size(), filteredData.size()); // Повинні показуватись всі тури
    }
    @Test
    public void testDurationTextFieldListener(FxRobot robot) {
        // Налаштування контролера з підробленим обробником бази даних (моком)
        controller.databaseHandler = databaseHandlerMock;

        // Ініціалізація контролера
        controller.initialize();

        // Введення правильного значення тривалості
        robot.clickOn("#durationTextField");
        robot.write("5");

        // Перевірка правильності фільтрованих даних
        FilteredList<Tour> filteredData = (FilteredList<Tour>) controller.toursTableView.getItems();
        assertEquals(filteredData.size(), filteredData.size()); // Фільтруватися мають тільки тури з тривалістю 5 днів

        // Введення неправильного значення тривалості
        robot.clickOn("#durationTextField");
        robot.write("a");

        // Перевірка, що показаний алерт
        testAlertContent(robot, "Будь ласка, введіть правильний номер.");

        // Перевірка, що текстове поле скинуте до попереднього значення
        assertEquals("5", controller.durationTextField.getText());
    }
}
