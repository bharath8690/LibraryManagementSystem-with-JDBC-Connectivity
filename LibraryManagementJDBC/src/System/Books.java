package System;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Books {
    private int bookId;
    private String book_name;
    private String author;
    private String department;
    private int totalCopies;
    private int availableCopies;

    // Constructor to initialize book details
    public Books(int bookId, String book_name, String author, String department, int totalCopies, int availableCopies) {
        this.bookId = bookId;
        this.book_name = book_name;
        this.author = author;
        this.department = department;
        this.totalCopies = totalCopies;
        this.availableCopies = availableCopies;
    }

    // Default constructor
    public Books() {}

    // Getter and Setter methods
    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public String getBookName() {
        return book_name;
    }

    public void setBookName(String book_name) {
        this.book_name = book_name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public int getTotalCopies() {
        return totalCopies;
    }

    public void setTotalCopies(int totalCopies) {
        this.totalCopies = totalCopies;
    }

    public int getAvailableCopies() {
        return availableCopies;
    }

    public void setAvailableCopies(int availableCopies) {
        this.availableCopies = availableCopies;
    }

    @Override
    public String toString() {
        return "[bookId=" + bookId + ", book_name=" + book_name + ", author=" + author +
               ", department=" + department + ", totalCopies=" + totalCopies +
               ", availableCopies=" + availableCopies + "]";
    }
    
    public boolean saveToDatabase() {
        String query = "INSERT INTO books (book_name, author, department, total_copies, available_copies) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, this.book_name);
            stmt.setString(2, this.author);
            stmt.setString(3, this.department);
            stmt.setInt(4, this.totalCopies);
            stmt.setInt(5, this.availableCopies);
            stmt.executeUpdate();
            System.out.println("Book saved successfully!");
            return true;
        } catch (SQLException e) {
            System.err.println("Failed to save book: " + e.getMessage());
            return false;
        }
    }

    
    public static Books getBookById(int bookId) {
        String query = "SELECT * FROM books WHERE book_id = ?";
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, bookId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Books(
                        rs.getInt("book_id"),
                        rs.getString("book_name"),
                        rs.getString("author"),
                        rs.getString("department"),
                        rs.getInt("total_copies"),
                        rs.getInt("available_copies")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching book: " + e.getMessage());
        }
        System.out.println("No book found with ID: " + bookId);
        return null;
    }

    
    public void updateAvailableCopies() {
        String query = "UPDATE books SET available_copies = ? WHERE book_id = ?";
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, this.availableCopies);
            stmt.setInt(2, this.bookId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}

