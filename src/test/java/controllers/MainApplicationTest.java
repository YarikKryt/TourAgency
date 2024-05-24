package controllers;

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(ApplicationExtension.class)
public class MainApplicationTest {

    private MainApplication app;
    private Stage stage; // Оголошення змінної stage

    @Start
    private void start(Stage stage) throws IOException {
        app = new MainApplication();
        app.start(stage);
        this.stage = stage; // Присвоєння значення stage
    }

    @Test
    public void testStartMethod(FxRobot robot) {
        // Arrange - вже виконано в методі start

        // Act & Assert
        assertEquals("Tour Agency", stage.getTitle(), "Title should be set to 'Tour Agency");
    }

    @Test
    public void testOpenNewSceneMethod() {
        // Arrange
        Platform.runLater(() -> {
            Button button = new Button();

            // Act & Assert (Exception case: FXML file not found)
            assertDoesNotThrow(() -> MainApplication.openNewScene(button, "invalid.fxml"));

            // Act & Assert (Successful case: FXML file found and loaded)
            assertDoesNotThrow(() -> MainApplication.openNewScene(button, "valid.fxml"));
            // Add further assertions to verify the behavior after successful loading, if necessary
        });
    }

}