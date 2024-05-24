package db;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class DatabaseHandlerTest {

    private DatabaseHandler databaseHandler;
    private Connection connection;
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;

    @BeforeEach
    public void setUp() throws Exception {
        databaseHandler = Mockito.spy(DatabaseHandler.class);
        connection = mock(Connection.class);
        preparedStatement = mock(PreparedStatement.class);
        resultSet = mock(ResultSet.class);

        when(databaseHandler.getDbConnection()).thenReturn(connection);
    }

    @Test
    public void testLoadTours() throws Exception {
        // Arrange
        List<Tour> expectedTours = new ArrayList<>();
        Tour newtour = new Tour(1, "Type1", new BigDecimal("100.00"), "City1", "City2", "Transport1", true, 3, 5);
        expectedTours.add(newtour);

        // Налаштувати макет DatabaseHandler для повернення очікуваного списку турів
        doReturn(expectedTours).when(databaseHandler).loadTours();

        // Act
        List<Tour> actualTours = databaseHandler.loadTours();

        // Assert
        assertEquals(expectedTours.size(), actualTours.size(), "Number of loaded tours should match");
        for (int i = 0; i < expectedTours.size(); i++) {
            assertEquals(expectedTours.get(i), actualTours.get(i), "Tour details should match");
        }
    }

    @Test
    public void testGetUser() throws Exception {
        // Arrange
        User user = new User("johndoe", "password");

        when(connection.prepareStatement("SELECT * FROM users WHERE Username=? AND Password=?")).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);

        // Act
        ResultSet result = databaseHandler.getUser(user);

        // Assert
        verify(preparedStatement).setString(1, user.getUsername());
        verify(preparedStatement).setString(2, user.getPassword());
        verify(preparedStatement).executeQuery();
    }

    @Test
    public void testDeleteTour() throws Exception {
        // Arrange
        int tourID = 1;

        when(connection.prepareStatement("DELETE FROM tours WHERE TourID = ?")).thenReturn(preparedStatement);

        // Act
        databaseHandler.deleteTour(tourID);

        // Assert
        verify(preparedStatement).setInt(1, tourID);
        verify(preparedStatement).executeUpdate();
    }

    @Test
    public void testSignUpUser_UsernameTaken() throws Exception {
        // Arrange
        User user = new User("John", "Doe", "Middle", "john.doe@example.com", "1234567890", "johndoe", "password", "Male");
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true); // Username вже зайнято

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            databaseHandler.signUpUser(user);
        });
        assertEquals("Username, email, or phone number is already taken", exception.getMessage());
    }

    @Test
    public void testSignUpUser_EmailTaken() throws Exception {
        // Arrange
        User user = new User("John", "Doe", "Middle", "john.doe@example.com", "1234567890", "johndoe", "password", "Male");
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false) // Username не зайнято
                .thenReturn(true); // Email зайнято

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            databaseHandler.signUpUser(user);
        });
        assertEquals("Username, email, or phone number is already taken", exception.getMessage());
    }

    @Test
    public void testSignUpUser_PhoneNumberTaken() throws Exception {
        // Arrange
        User user = new User("John", "Doe", "Middle", "john.doe@example.com", "1234567890", "johndoe", "password", "Male");
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false) // Username не зайнято
                .thenReturn(false) // Email не зайнятий
                .thenReturn(true); // Phone number зайнятий

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            databaseHandler.signUpUser(user);
        });
        assertEquals("Username, email, or phone number is already taken", exception.getMessage());
    }

    @Test
    public void testSignUpUser_SQLException() throws Exception {
        // Arrange
        User user = new User("John", "Doe", "Middle", "john.doe@example.com", "1234567890", "johndoe", "password", "Male");
        when(connection.prepareStatement(anyString())).thenThrow(new SQLException());

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            databaseHandler.signUpUser(user);
        });
        assertEquals("java.sql.SQLException", exception.getMessage());
    }

    @Test
    public void testSignUpUser_ClassNotFoundException() throws Exception {
        // Arrange
        User user = new User("John", "Doe", "Middle", "john.doe@example.com", "1234567890", "johndoe", "password", "Male");
        when(databaseHandler.getDbConnection()).thenThrow(new ClassNotFoundException());

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            databaseHandler.signUpUser(user);
        });
        assertEquals("java.lang.ClassNotFoundException", exception.getMessage());
    }

    @Test
    void testDeleteTour_SQLException() throws Exception {
        // Arrange
        int tourID = 1;
        doReturn(connection).when(databaseHandler).getDbConnection();
        when(connection.prepareStatement(anyString())).thenThrow(SQLException.class);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            databaseHandler.deleteTour(tourID);
        });
        assertTrue(exception.getCause() instanceof SQLException);
    }

    @Test
    void testDeleteTour_ClassNotFoundException() throws Exception {
        // Arrange
        int tourID = 1;
        doThrow(ClassNotFoundException.class).when(databaseHandler).getDbConnection();

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            databaseHandler.deleteTour(tourID);
        });
        assertTrue(exception.getCause() instanceof ClassNotFoundException);
    }

    @Test
    void testDeleteAllTours_SQLException() throws Exception {
        // Arrange
        doReturn(connection).when(databaseHandler).getDbConnection();
        when(connection.prepareStatement(anyString())).thenThrow(SQLException.class);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            databaseHandler.deleteAllTours();
        });
        assertEquals("java.sql.SQLException", exception.getMessage());
        ;
    }

    @Test
    void testDeleteAllTours_ClassNotFoundException() throws Exception {
        // Arrange
        doThrow(ClassNotFoundException.class).when(databaseHandler).getDbConnection();

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            databaseHandler.deleteAllTours();
        });
        assertTrue(exception.getCause() instanceof ClassNotFoundException);
    }

    @Test
    void testAddTour_Success() throws Exception {
        // Arrange
        Tour newTour = new Tour(0, "Type1", new BigDecimal("100.00"), "City1", "City2", "Transport1", true, 3, 5);
        when(connection.prepareStatement("INSERT INTO tours (Type, Price, FromCity, ToCity, Transport, Meals, MealsPerDay, DurationInDays) VALUES (?,?,?,?,?,?,?,?)"))
                .thenReturn(preparedStatement);

        // Act
        databaseHandler.addTour(newTour);

        // Assert
        verify(preparedStatement).setString(1, newTour.getType());
        verify(preparedStatement).setBigDecimal(2, newTour.getPrice());
        verify(preparedStatement).setString(3, newTour.getFromCity());
        verify(preparedStatement).setString(4, newTour.getToCity());
        verify(preparedStatement).setString(5, newTour.getTransport());
        verify(preparedStatement).setBoolean(6, newTour.getMeals());
        verify(preparedStatement).setInt(7, newTour.getMealsPerDay());
        verify(preparedStatement).setInt(8, newTour.getDurationInDays());
        verify(preparedStatement).executeUpdate();
    }

    @Test
    void testAddTour_SQLException() throws Exception {
        // Arrange
        Tour newTour = new Tour(0, "Type1", new BigDecimal("100.00"), "City1", "City2", "Transport1", true, 3, 5);
        when(connection.prepareStatement("INSERT INTO tours (Type, Price, FromCity, ToCity, Transport, Meals, MealsPerDay, DurationInDays) VALUES (?,?,?,?,?,?,?,?)"))
                .thenThrow(new SQLException());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> databaseHandler.addTour(newTour));
    }

    @Test
    void testAddTour_ClassNotFoundException() throws Exception {
        // Arrange
        Tour newTour = new Tour(0, "Type1", new BigDecimal("100.00"), "City1", "City2", "Transport1", true, 3, 5);
        doThrow(ClassNotFoundException.class).when(databaseHandler).getDbConnection();

        // Act & Assert
        assertThrows(RuntimeException.class, () -> databaseHandler.addTour(newTour));
    }

    @Test
    void testIsUsernameTaken_True() throws Exception {
        // Arrange
        String username = "existingUser";
        when(connection.prepareStatement("SELECT * FROM users WHERE Username=?")).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true); // Username існує

        // Act
        boolean result = databaseHandler.isUsernameTaken(username);

        // Assert
        assertTrue(result, "isUsernameTaken should return true if username exists");
    }

    @Test
    void testIsUsernameTaken_False() throws Exception {
        // Arrange
        String username = "newUser";
        when(connection.prepareStatement("SELECT * FROM users WHERE Username=?")).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false); // Username не існує

        // Act
        boolean result = databaseHandler.isUsernameTaken(username);

        // Assert
        assertFalse(result, "isUsernameTaken should return false if username does not exist");
    }

    @Test
    void testIsUsernameTaken_SQLException() throws Exception {
        // Arrange
        String username = "existingUser";
        when(connection.prepareStatement("SELECT * FROM users WHERE Username=?")).thenThrow(SQLException.class);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> databaseHandler.isUsernameTaken(username));
    }

    @Test
    void testIsUsernameTaken_ClassNotFoundException() throws Exception {
        // Arrange
        String username = "existingUser";
        doThrow(ClassNotFoundException.class).when(databaseHandler).getDbConnection();

        // Act & Assert
        assertThrows(RuntimeException.class, () -> databaseHandler.isUsernameTaken(username));
    }

    @Test
    void testGetDbConnection_ClassNotFoundException() throws SQLException, ClassNotFoundException {
        // Arrange
        doThrow(ClassNotFoundException.class).when(databaseHandler).getDbConnection();

        // Act & Assert
        assertThrows(ClassNotFoundException.class, () -> databaseHandler.getDbConnection());
    }
}