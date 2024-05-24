package db;

import logger.Logger;

import java.util.ArrayList;
import java.util.List;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
public class DatabaseHandler extends Configs {
    Connection dbConnection;

    public Connection getDbConnection() throws ClassNotFoundException, SQLException {
        String connectionString = "jdbc:mysql://" + dbHost + ":" + dbPort + "/" + dbName;

        Class.forName("com.mysql.cj.jdbc.Driver");
        dbConnection = DriverManager.getConnection(connectionString, dbUser, dbPass);

        return dbConnection;
    }

    public void signUpUser(User user){
        String insert = "INSERT INTO " + Const.USERS_TABLE +
                "(" + Const.USERS_FIRSTNAME + "," + Const.USERS_LASTNAME + "," + Const.USERS_MIDDLENAME + "," + Const.USERS_EMAIL +
                "," + Const.USERS_PHONENUMBER + "," + Const.USERS_USERNAME + "," + Const.USERS_PASSWORD + "," + Const.USERS_GENDER +
                ")" + "VALUES(?,?,?,?,?,?,?,?)";
        try {
            // Перевірка чи логін, пошта, або ж номер телефону вже зайняті
            if (isUsernameTaken(user.getUsername()) || isEmailTaken(user.getEmail()) || isPhoneNumberTaken(user.getPhoneNumber())) {
                throw new RuntimeException("Username, email, or phone number is already taken");
            }
            String hashedPassword = PasswordHasher.hashPassword(user.getPassword());
            PreparedStatement prSt = getDbConnection().prepareStatement(insert);
            prSt.setString(1, user.getFirstName());
            prSt.setString(2, user.getLastName());
            prSt.setString(3, user.getMiddleName());
            prSt.setString(4, user.getEmail());
            prSt.setString(5, user.getPhoneNumber());
            prSt.setString(6, user.getUsername());
            prSt.setString(7, hashedPassword);
            prSt.setString(8, user.getGender());

            prSt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Помилка вставки даних у базу даних");
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    boolean isUsernameTaken(String username) {
        String query = "SELECT * FROM " + Const.USERS_TABLE + " WHERE " + Const.USERS_USERNAME + "=?";
        try (PreparedStatement prSt = getDbConnection().prepareStatement(query)) {
            prSt.setString(1, username);
            ResultSet resultSet = prSt.executeQuery();
            return resultSet.next(); // повертає true, якщо новий рядок існує
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    boolean isEmailTaken(String email) {
        String query = "SELECT * FROM " + Const.USERS_TABLE + " WHERE " + Const.USERS_EMAIL + "=?";
        try (PreparedStatement prSt = getDbConnection().prepareStatement(query)) {
            prSt.setString(1, email);
            ResultSet resultSet = prSt.executeQuery();
            return resultSet.next(); // повертає true, якщо новий рядок існує
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    boolean isPhoneNumberTaken(String phoneNumber) {
        String query = "SELECT * FROM " + Const.USERS_TABLE + " WHERE " + Const.USERS_PHONENUMBER + "=?";
        try (PreparedStatement prSt = getDbConnection().prepareStatement(query)) {
            prSt.setString(1, phoneNumber);
            ResultSet resultSet = prSt.executeQuery();
            return resultSet.next(); // повертає true, якщо новий рядок існує
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public ResultSet getUser(User user) {
        ResultSet resSet = null;

        String select = "SELECT * FROM " + Const.USERS_TABLE + " WHERE " +
                Const.USERS_USERNAME + "=? AND " + Const.USERS_PASSWORD + "=?";

        try {
            PreparedStatement prSt = getDbConnection().prepareStatement(select);
            prSt.setString(1, user.getUsername());
            prSt.setString(2, user.getPassword());

            resSet = prSt.executeQuery();
        } catch (SQLException e) {
            Logger.sendEmail("TourAgency critical error: Error getting user from the database", e);
            Logger.writeLineToFile(Logger.getCurrentTimestamp() + " Error getting user from the database");
        } catch (ClassNotFoundException e) {
            Logger.sendEmail("TourAgency critical error: Error loading database driver",e);
            Logger.writeLineToFile(Logger.getCurrentTimestamp() + " Error loading the database driver");
        }

        return resSet;
    }

    public List<Tour> loadTours() {
        List<Tour> tours = new ArrayList<>();

        try (Connection connection = getDbConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM tours");
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Tour tour = new Tour(
                        resultSet.getInt("TourID"),
                        resultSet.getString("Type"),
                        resultSet.getBigDecimal("Price"),
                        resultSet.getString("FromCity"),
                        resultSet.getString("ToCity"),
                        resultSet.getString("Transport"),
                        resultSet.getBoolean("Meals"),
                        resultSet.getInt("MealsPerDay"),
                        resultSet.getByte("DurationInDays")
                );
                tour.setTourID(resultSet.getInt("TourID"));
                tour.setType(resultSet.getString("Type"));
                tour.setPrice(resultSet.getBigDecimal("Price"));
                tour.setFromCity(resultSet.getString("FromCity"));
                tour.setToCity(resultSet.getString("ToCity"));
                tour.setTransport(resultSet.getString("Transport"));
                tour.setMeals(resultSet.getBoolean("Meals"));
                tour.setMealsPerDay(resultSet.getInt("MealsPerDay"));
                tour.setDurationInDays(resultSet.getByte("DurationInDays"));

                tours.add(tour);
            }
        } catch (SQLException e) {
            Logger.writeLineToFile(Logger.getCurrentTimestamp() + " Error connecting to the database");
            Logger.sendEmail("TourAgency critical error: Error connecting to the database", e);
        } catch (ClassNotFoundException e) {
            Logger.writeLineToFile(Logger.getCurrentTimestamp() + " Error loading the database driver: " + e.getMessage());
            Logger.sendEmail("TourAgency critical error: Error loading database driver", e);
        }

        return tours;
    }

    public void addTour(Tour tour) {
        String insert = "INSERT INTO tours (Type, Price, FromCity, ToCity, Transport, Meals, MealsPerDay, DurationInDays) VALUES (?,?,?,?,?,?,?,?)";
        try (Connection connection = getDbConnection()) {
            PreparedStatement statement = connection.prepareStatement(insert);
            statement.setString(1, tour.getType());
            statement.setBigDecimal(2, tour.getPrice());
            statement.setString(3, tour.getFromCity());
            statement.setString(4, tour.getToCity());
            statement.setString(5, tour.getTransport());
            statement.setBoolean(6, tour.getMeals());
            statement.setInt(7, tour.getMealsPerDay());
            statement.setInt(8, tour.getDurationInDays());

            statement.executeUpdate();
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    public void deleteTour(int tourID) {
        String delete = "DELETE FROM tours WHERE TourID = ?";
        try (Connection connection = getDbConnection()) {
            PreparedStatement statement = connection.prepareStatement(delete);
            statement.setInt(1, tourID);
            statement.executeUpdate();
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteAllTours() {
        String deleteAll = "TRUNCATE TABLE tours";
        try (Connection connection = getDbConnection()) {
            PreparedStatement statement = connection.prepareStatement(deleteAll);
            statement.executeUpdate();
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}
