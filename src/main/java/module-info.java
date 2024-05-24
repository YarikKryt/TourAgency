module touragency.touragency {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires mysql.connector.java;
    requires java.mail;
    requires org.apache.commons.validator;


    opens controllers to javafx.fxml;
    exports controllers;
    exports db;
}