package System;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class LibraryData {
    static Map<String, Users> userDetails = new ConcurrentHashMap<>();
    static Map<Integer, Books> bookDetails = new ConcurrentHashMap<>();

    public static void loadUsers() {
        String query = " SELECT user_id, username, password, role, department FROM users";
       


        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Users user = new Users(
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("role"),
                        rs.getString("department")
                );
                userDetails.put(user.getUserName(), user);
            }
        } catch (SQLException e) {
            System.err.println("Error loading users from the database: " + e.getMessage());
        }
    }

    public static void loadBooks() {
    	String query = "SELECT book_id, book_name, author, department, total_copies, available_copies FROM books";
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Books book = new Books(
                        rs.getInt("book_id"),
                        rs.getString("book_name"),
                        rs.getString("author"),
                        rs.getString("department"),
                        rs.getInt("total_copies"),
                        rs.getInt("available_copies")
                );
                bookDetails.put(book.getBookId(), book);
            }
        } catch (SQLException e) {
            System.err.println("Error loading books from the database: " + e.getMessage());
        }
    }
}
