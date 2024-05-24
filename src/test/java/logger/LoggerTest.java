package logger;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

public class LoggerTest {
    @TempDir
    Path tempDir;

    @Test
    public void testWriteLineToFileWithActualFile() throws IOException, InterruptedException {
        // Act
        Logger.writeLineToFile("Test log line");

        // Assert
        File logFile = new File(Logger.LOG_FILE);
        assertTrue(logFile.exists());
        assertTrue(Files.readAllLines(logFile.toPath()).contains("Test log line"));

    }
    @Test
    public void testSendEmail() {
        // Arrange
        String message = "Test error message";
        Throwable throwable = new RuntimeException("Test exception");
        EmailSender emailSenderMock = mock(EmailSender.class);

        // Act
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        Logger.sendEmail(message, throwable);

        // Assert
        assertTrue(outContent.toString().contains("Email sent successfully."));
    }
}