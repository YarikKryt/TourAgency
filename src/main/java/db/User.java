package db;

import java.util.Objects;

public class User {
    private String firstName;
    private String lastName;
    private String MiddleName;
    private String Email;
    private String PhoneNumber;
    private String Username;
    private String Password;
    private String Gender;

    public User(String username, String password) {
        Username = username;
        Password = password;
    }

    public User(String firstName, String lastName, String middleName, String email, String phoneNumber, String username, String password, String gender) {
        this.firstName = firstName;
        this.lastName = lastName;
        MiddleName = middleName;
        Email = email;
        PhoneNumber = phoneNumber;
        Username = username;
        Password = password;
        Gender = gender;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(firstName, user.firstName) &&
                Objects.equals(lastName, user.lastName) &&
                Objects.equals(MiddleName, user.MiddleName) &&
                Objects.equals(Email, user.Email) &&
                Objects.equals(PhoneNumber, user.PhoneNumber) &&
                Objects.equals(Username, user.Username) &&
                Objects.equals(Password, user.Password) &&
                Objects.equals(Gender, user.Gender);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstName, lastName, MiddleName, Email, PhoneNumber, Username, Password, Gender);
    }

    public User() {}

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMiddleName() {
        return MiddleName;
    }

    public void setMiddleName(String middleName) {
        MiddleName = middleName;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        PhoneNumber = phoneNumber;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getGender() {
        return Gender;
    }

    public void setGender(String gender) {
        Gender = gender;
    }
}
