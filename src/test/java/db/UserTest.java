package db;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void testUserConstructorWithUsernameAndPassword() {
        User user = new User("testUser", "testPassword");
        assertEquals("testUser", user.getUsername());
        assertEquals("testPassword", user.getPassword());
        assertNull(user.getFirstName());
        assertNull(user.getLastName());
        assertNull(user.getMiddleName());
        assertNull(user.getEmail());
        assertNull(user.getPhoneNumber());
        assertNull(user.getGender());
    }

    @Test
    void testUserConstructorWithAllFields() {
        User user = new User("John", "Doe", "Middle", "john.doe@example.com", "1234567890", "johndoe", "password123", "Male");
        assertEquals("John", user.getFirstName());
        assertEquals("Doe", user.getLastName());
        assertEquals("Middle", user.getMiddleName());
        assertEquals("john.doe@example.com", user.getEmail());
        assertEquals("1234567890", user.getPhoneNumber());
        assertEquals("johndoe", user.getUsername());
        assertEquals("password123", user.getPassword());
        assertEquals("Male", user.getGender());
    }

    @Test
    void testEqualsAndHashCode() {
        User user1 = new User("John", "Doe", "Middle", "john.doe@example.com", "1234567890", "johndoe", "password123", "Male");
        User user2 = new User("John", "Doe", "Middle", "john.doe@example.com", "1234567890", "johndoe", "password123", "Male");
        User user3 = new User("Jane", "Doe", "Middle", "jane.doe@example.com", "0987654321", "janedoe", "password456", "Female");

        assertEquals(user1, user2);
        assertNotEquals(user1, user3);
        assertEquals(user1.hashCode(), user2.hashCode());
        assertNotEquals(user1.hashCode(), user3.hashCode());
    }

    @Test
    void testGettersAndSetters() {
        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setMiddleName("Middle");
        user.setEmail("john.doe@example.com");
        user.setPhoneNumber("1234567890");
        user.setUsername("johndoe");
        user.setPassword("password123");
        user.setGender("Male");

        assertEquals("John", user.getFirstName());
        assertEquals("Doe", user.getLastName());
        assertEquals("Middle", user.getMiddleName());
        assertEquals("john.doe@example.com", user.getEmail());
        assertEquals("1234567890", user.getPhoneNumber());
        assertEquals("johndoe", user.getUsername());
        assertEquals("password123", user.getPassword());
        assertEquals("Male", user.getGender());
    }

    @Test
    void testDefaultConstructor() {
        User user = new User();
        assertNull(user.getFirstName());
        assertNull(user.getLastName());
        assertNull(user.getMiddleName());
        assertNull(user.getEmail());
        assertNull(user.getPhoneNumber());
        assertNull(user.getUsername());
        assertNull(user.getPassword());
        assertNull(user.getGender());
    }
}
