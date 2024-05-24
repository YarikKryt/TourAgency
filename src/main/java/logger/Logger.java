package logger;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Logger {
    static final String LOG_FILE = "logger.txt";
    private static final String EMAIL_SUBJECT = "Critical Error in Application";
    private static final String EMAIL_BODY = "An error occurred in the application: %s";

    public static String getCurrentTimestamp() {
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return currentDateTime.format(formatter);
    }

    public static void writeLineToFile(String line) {
        try {
            File file = new File(LOG_FILE);
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fw = new FileWriter(file, true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(line);
            bw.newLine();
            bw.close();
        } catch (IOException e) {
            System.err.println("An error occurred while writing to the file: " + e.getMessage());
        }
    }

    public static void sendEmail(String message, Throwable throwable) {
        String body = String.format(EMAIL_BODY, message);
        if (throwable != null) {
            body += "\nStack trace: " + throwable.getStackTrace();
        }
        EmailSender.emailMsg(EMAIL_SUBJECT, body);
    }
}
