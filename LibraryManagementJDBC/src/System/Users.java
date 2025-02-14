package System;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class Users {
    private int userId; // Unique identifier for the user
    private String userName;
    private String password;
    private String role;
    private String department;

    // Constructor to initialize user details
    public Users(int userId, String userName, String password, String role, String department) {
        this.userId = userId;
        this.userName = userName;
        this.password = password;
        this.role = role;
        this.department = department;
    }

    // Default constructor
    public Users() {}

    // Getter and Setter methods
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    public String getDepartment() {
        return department;
    }

    @Override
    public String toString() {
        return "[UserId=" + userId + ", UserName=" + userName + ", Password=" + password + ", Role=" + role + ", Department=" + department + "]";
    }

    // Method to save user details to the database
    public boolean saveToDatabase() {
        String query = "INSERT INTO users (username, password, role, department) VALUES (?, ?, ?, ?)";
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, this.userName);
            stmt.setString(2, this.password);
            stmt.setString(3, this.role);
            stmt.setString(4, this.department);
            stmt.executeUpdate();
            System.out.println("User saved successfully!");
            return true;
        } catch (SQLException e) {
            System.err.println("Failed to save user: " + e.getMessage());
            return false;
        }
    }


    
   

    public static Optional<Users> getUserByUsername(String username) {
        String query = "SELECT user_id, username, password, role, department FROM users WHERE username = ?";
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Users user = new Users(
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("role"),
                        rs.getString("department")
                    );
                    return Optional.of(user);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching user: " + e.getMessage());
        }
        return Optional.empty();
    }


}

