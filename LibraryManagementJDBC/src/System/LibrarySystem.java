package System;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.Scanner;

public class LibrarySystem {
    static Scanner scanner = new Scanner(System.in);
    Users user = new Users();

    public static void main(String[] args) {
        LibraryData.loadUsers();
        LibraryData.loadBooks();
        LibrarySystem system = new LibrarySystem();
        system.welcome();
    }

    public void welcome() {
        System.out.println("Welcome to the Library!!");
        System.out.println("Login(1)/SignUp(2)/Exit(3)");
        String choice = scanner.next();
        switch (choice) {
            case "1":
                login();
                break;
            case "2":
                signUp();
                break;
            case "3":
                System.exit(0);
                break;
            default:
                System.out.println("Invalid Choice! Please try again!!");
                welcome();
                break;
        }
    }

    public void signUp() {
        System.out.println("Enter your desired username: ");
        String username = scanner.next();

        // Check if the username already exists
        if (LibraryData.userDetails.containsKey(username)) {
            System.out.println("Username already taken. Please choose a different username.");
            return;
        }

        System.out.println("Enter your password: ");
        String password = scanner.next();
        System.out.println("Enter your role (Student/Professor/Librarian): ");
        String role = scanner.next();
        System.out.println("Enter your department: ");
        String department = scanner.next();

        // Insert the new user into the database
        String query = "INSERT INTO users (username, password, role, department) VALUES (?, ?, ?, ?)";
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, role);
            stmt.setString(4, department);
            stmt.executeUpdate();

            // Retrieve the generated user_id
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int userId = generatedKeys.getInt(1);

                    // Create a new Users object and add it to the in-memory map
                    Users newUser = new Users(userId, username, password, role, department);
                    LibraryData.userDetails.put(username, newUser);

                    System.out.println("Successfully signed up! You can now log in with your new credentials.");
                } else {
                    System.out.println("Sign-up failed: Unable to retrieve user ID.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("An error occurred during sign-up. Please try again.");
        }
    }

    public void login() {
        System.out.println("Enter your Name:");
        String name = scanner.next();
        System.out.println("Enter your Password:");
        String password = scanner.next();

        Users checkName = LibraryData.userDetails.get(name);

        if (checkName == null) {
            System.out.println("User not found! Do you want to sign up? (yes/no)");
            if (scanner.next().equalsIgnoreCase("yes")) {
                signUp();
            } else {
                System.out.println("Thank You!!");
            }
            return;
        }

        if (password.equals(checkName.getPassword())) {
            System.out.println("Access Granted!! Welcome " + name);
            user = new Users(checkName.getUserId(), name, password, checkName.getRole(), checkName.getDepartment());

            switch (user.getRole().toLowerCase()) {
                case "student":
                    studentMenu();
                    break;
                case "professor":
                    professorMenu();
                    break;
                case "librarian":
                    librarianMenu();
                    break;
                default:
                    System.out.println("Invalid role! Please contact the administrator.");
                    break;
            }
        } else {
            System.out.println("Invalid password! Try again.");
            login();
        }
    }

    public void studentMenu() {
        while (true) {
            System.out.println("\nStudent Menu:");
            System.out.println("1. View Available Books");
            System.out.println("2. Checkout Book");
            System.out.println("3. Return Book");
            System.out.println("4. Logout");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    viewAvailableBooks();
                    break;
                case 2:
                    checkoutBook();
                    break;
                case 3:
                    returnBook();
                    break;
                case 4:
                    System.out.println("Logging out...");
                    return;
                default:
                    System.out.println("Invalid choice! Please try again.");
            }
        }
    }

    public void professorMenu() {
        while (true) {
            System.out.println("\nProfessor Menu:");
            System.out.println("1. View Available Books");
            System.out.println("2. Checkout Book");
            System.out.println("3. Return Book");
            System.out.println("4. Request New Book");
            System.out.println("5. Logout");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    viewAvailableBooks();
                    break;
                case 2:
                    checkoutBook();
                    break;
                case 3:
                    returnBook();
                    break;
                case 4:
                    requestNewBook();
                    break;
                case 5:
                    System.out.println("Logging out...");
                    return;
                default:
                    System.out.println("Invalid choice! Please try again.");
            }
        }
    }

    public void librarianMenu() {
        while (true) {
            System.out.println("\nLibrarian Menu:");
            System.out.println("1. View All Books");
            System.out.println("2. Add New Book");
            System.out.println("3. Remove Book");
            System.out.println("4. View All Users");
            System.out.println("5. Remove User");
            System.out.println("6. Logout");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    viewAllBooks();
                    break;
                case 2:
                    addNewBook();
                    break;
                case 3:
                    removeBook();
                    break;
                case 4:
                    viewAllUsers();
                    break;
                case 5:
                    removeUser();
                    break;
                case 6:
                    System.out.println("Logging out...");
                    return;
                default:
                	System.out.println("Invalid choice! Please try again.");
            }
        }
    }
    
    public void viewAvailableBooks() {
        System.out.println("\nAvailable Books in Your Department:");
        System.out.println("ID\tTitle\t\tAuthor\t\tAvailable Copies");
        System.out.println("---------------------------------------------------------------");

        LibraryData.bookDetails.values().stream()
            .filter(book -> book.getDepartment().equalsIgnoreCase(user.getDepartment()) && book.getAvailableCopies() > 0)
            .forEach(book -> System.out.printf("%d\t%s\t\t%s\t\t%d%n",
                    book.getBookId(),
                    book.getBookName(),
                    book.getAuthor(),
                    book.getAvailableCopies()));
    }
    
    public void checkoutBook() {
        System.out.println("\nEnter the ID of the book you want to check out:");
        int bookId = scanner.nextInt();

        // Retrieve the book from the BookDetails map
        Books selectedBook = LibraryData.bookDetails.get(bookId);

        // Validate book selection
        if (selectedBook == null) {
            System.out.println("Invalid book ID. Please try again.");
            return;
        }

        // Check department restriction for students
        if (user.getRole().equalsIgnoreCase("Student") &&
            !selectedBook.getDepartment().equalsIgnoreCase(user.getDepartment())) {
            System.out.println("You can only check out books from your own department.");
            return;
        }

        // Check if the book is available
        if (selectedBook.getAvailableCopies() <= 0) {
            System.out.println("The book is currently not available.");
            return;
        }

        // Calculate the due date (14 days from today)
        LocalDate checkoutDate = LocalDate.now();
        LocalDate dueDate = checkoutDate.plusDays(14);

        // SQL queries
        String updateBookQuery = "UPDATE books SET available_copies = ? WHERE book_id = ?";
        String insertCheckoutQuery = "INSERT INTO checkouts (user_id, book_id, checkout_date, due_date) VALUES (?, ?, ?, ?)";

        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement updateBookStmt = conn.prepareStatement(updateBookQuery);
             PreparedStatement insertCheckoutStmt = conn.prepareStatement(insertCheckoutQuery)) {

            // Start transaction
            conn.setAutoCommit(false);

            // Update available copies in the database
            updateBookStmt.setInt(1, selectedBook.getAvailableCopies() - 1);
            updateBookStmt.setInt(2, bookId);
            updateBookStmt.executeUpdate();

            // Insert checkout record into the database
            insertCheckoutStmt.setInt(1, user.getUserId()); // Ensure user object has userId
            insertCheckoutStmt.setInt(2, bookId);
            insertCheckoutStmt.setDate(3, java.sql.Date.valueOf(checkoutDate));
            insertCheckoutStmt.setDate(4, java.sql.Date.valueOf(dueDate));
            insertCheckoutStmt.executeUpdate();

            // Commit transaction
            conn.commit();

            // Update the in-memory BookDetails map
            selectedBook.setAvailableCopies(selectedBook.getAvailableCopies() - 1);

            System.out.println("You have successfully checked out: " + selectedBook.getBookName());
            System.out.println("Checkout Date: " + checkoutDate);
            System.out.println("Due Date for return: " + dueDate);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void returnBook() {
        System.out.println("\nEnter the ID of the book you want to return:");
        int bookId = scanner.nextInt();

        // Retrieve the book from the BookDetails map
        Books selectedBook = LibraryData.bookDetails.get(bookId);

        // Validate book selection
        if (selectedBook == null) {
            System.out.println("Invalid book ID. Please try again.");
            return;
        }

        // Update the database
        String updateBookQuery = "UPDATE books SET available_copies = ? WHERE book_id = ?";
        String updateCheckoutQuery = "UPDATE checkouts SET return_date = ? WHERE user_id = ? AND book_id = ? AND return_date IS NULL";

        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement updateBookStmt = conn.prepareStatement(updateBookQuery);
             PreparedStatement updateCheckoutStmt = conn.prepareStatement(updateCheckoutQuery)) {

            // Start transaction
            conn.setAutoCommit(false);

            // Update available copies
            updateBookStmt.setInt(1, selectedBook.getAvailableCopies() + 1);
            updateBookStmt.setInt(2, bookId);
            updateBookStmt.executeUpdate();

            // Update checkout record with return date
            updateCheckoutStmt.setDate(1, java.sql.Date.valueOf(LocalDate.now()));
            updateCheckoutStmt.setInt(2, user.getUserId());
            updateCheckoutStmt.setInt(3, bookId);
            int rowsAffected = updateCheckoutStmt.executeUpdate();

            if (rowsAffected == 0) {
                System.out.println("No matching checkout record found. Please ensure you have checked out this book.");
                conn.rollback();
                return;
            }

            // Commit transaction
            conn.commit();

            // Update the in-memory BookDetails map
            selectedBook.setAvailableCopies(selectedBook.getAvailableCopies() + 1);

            System.out.println("You have successfully returned: " + selectedBook.getBookName());

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void viewAllAvailableBooks() {
        System.out.println("\nAll Available Books in the Library:");
        System.out.println("ID\tTitle\t\tAuthor\t\tDepartment\tAvailable Copies");
        System.out.println("--------------------------------------------------------------------------");

        LibraryData.bookDetails.values().stream()
            .filter(book -> book.getAvailableCopies() > 0)
            .forEach(book -> System.out.printf("%d\t%s\t\t%s\t\t%s\t\t%d%n",
                    book.getBookId(),
                    book.getBookName(),
                    book.getAuthor(),
                    book.getDepartment(),
                    book.getAvailableCopies()));
    }

    
    public void requestNewBook() {
        System.out.println("\nEnter the title of the book you want to request:");
        String title = scanner.nextLine();
        System.out.println("Enter the author of the book:");
        String author = scanner.nextLine();
        System.out.println("Enter the department for the book:");
        String department = scanner.nextLine();
        System.out.println("Enter the number of copies requested:");
        int copiesRequested = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        String insertRequestQuery = "INSERT INTO book_requests (title, author, department, copies_requested, requested_by) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement insertRequestStmt = conn.prepareStatement(insertRequestQuery)) {

            insertRequestStmt.setString(1, title);
            insertRequestStmt.setString(2, author);
            insertRequestStmt.setString(3, department);
            insertRequestStmt.setInt(4, copiesRequested);
            insertRequestStmt.setInt(5, user.getUserId()); // Ensure user object has userId
            insertRequestStmt.executeUpdate();

            System.out.println("Your request for the book \"" + title + "\" has been submitted successfully.");

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("An error occurred while submitting your book request. Please try again.");
        }
    }

    public void viewAllBooks() {
        System.out.println("\nAll Books in the Library:");
        System.out.println("ID\tTitle\t\tAuthor\t\tDepartment\tTotal Copies\tAvailable Copies");
        System.out.println("----------------------------------------------------------------------------------------");

        LibraryData.bookDetails.values().forEach(book -> System.out.printf("%d\t%s\t\t%s\t\t%s\t\t%d\t\t%d%n",
                book.getBookId(),
                book.getBookName(),
                book.getAuthor(),
                book.getDepartment(),
                book.getTotalCopies(),
                book.getAvailableCopies()));
    }

    
    public void addNewBook() {
        // Display pending book requests
        displayPendingBookRequests();

        System.out.println("\nDo you want to add a book from the pending requests? (yes/no)");
        String choice = scanner.nextLine();

        if (choice.equalsIgnoreCase("yes")) {
            System.out.println("Enter the Request ID of the book to add:");
            int requestId = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            // Fetch the book request details
            String fetchRequestQuery = "SELECT title, author, department, copies_requested FROM book_requests WHERE request_id = ?";
            try (Connection conn = DataBaseConnection.getConnection();
                 PreparedStatement fetchStmt = conn.prepareStatement(fetchRequestQuery)) {

                fetchStmt.setInt(1, requestId);
                try (ResultSet rs = fetchStmt.executeQuery()) {
                    if (rs.next()) {
                        String title = rs.getString("title");
                        String author = rs.getString("author");
                        String department = rs.getString("department");
                        int copiesRequested = rs.getInt("copies_requested");

                        // Add the book to the library
                        String insertBookQuery = "INSERT INTO books (book_name, author, department, total_copies, available_copies) VALUES (?, ?, ?, ?, ?)";
                        try (PreparedStatement insertStmt = conn.prepareStatement(insertBookQuery)) {
                            insertStmt.setString(1, title);
                            insertStmt.setString(2, author);
                            insertStmt.setString(3, department);
                            insertStmt.setInt(4, copiesRequested);
                            insertStmt.setInt(5, copiesRequested);
                            insertStmt.executeUpdate();

                            // Update the book request status to 'Fulfilled'
                            String updateRequestQuery = "UPDATE book_requests SET status = 'Fulfilled' WHERE request_id = ?";
                            try (PreparedStatement updateStmt = conn.prepareStatement(updateRequestQuery)) {
                                updateStmt.setInt(1, requestId);
                                updateStmt.executeUpdate();
                            }

                            System.out.println("The book \"" + title + "\" has been added successfully.");
                        }
                    } else {
                        System.out.println("Invalid Request ID. Please try again.");
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("An error occurred while adding the new book.");
            }
        } else {
            // Proceed with adding a new book manually
            System.out.println("Enter the title of the new book:");
            String title = scanner.nextLine();
            System.out.println("Enter the author of the book:");
            String author = scanner.nextLine();
            System.out.println("Enter the department for the book:");
            String department = scanner.nextLine();
            System.out.println("Enter the total number of copies:");
            int totalCopies = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            String insertBookQuery = "INSERT INTO books (book_name, author, department, total_copies, available_copies) VALUES (?, ?, ?, ?, ?)";
            try (Connection conn = DataBaseConnection.getConnection();
                 PreparedStatement insertStmt = conn.prepareStatement(insertBookQuery)) {

                insertStmt.setString(1, title);
                insertStmt.setString(2, author);
                insertStmt.setString(3, department);
                insertStmt.setInt(4, totalCopies);
                insertStmt.setInt(5, totalCopies);
                insertStmt.executeUpdate();

                System.out.println("The book \"" + title + "\" has been added successfully.");
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("An error occurred while adding the new book.");
            }
        }
    }

    public void removeBook() {
        System.out.println("\nEnter the ID of the book you want to remove:");
        int bookId = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        // Check if the book exists
        Books selectedBook = LibraryData.bookDetails.get(bookId);
        if (selectedBook == null) {
            System.out.println("Invalid book ID. Please try again.");
            return;
        }

        String deleteBookQuery = "DELETE FROM books WHERE book_id = ?";

        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement deleteBookStmt = conn.prepareStatement(deleteBookQuery)) {

            deleteBookStmt.setInt(1, bookId);
            deleteBookStmt.executeUpdate();

            // Remove the book from the in-memory BookDetails map
            LibraryData.bookDetails.remove(bookId);

            System.out.println("The book \"" + selectedBook.getBookName() + "\" has been removed successfully.");

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("An error occurred while removing the book. Please try again.");
        }
    }

    
    public void viewAllUsers() {
        System.out.println("\nAll Registered Users:");
        System.out.println("Username\tRole\t\tDepartment");
        System.out.println("-------------------------------------------");

        LibraryData.userDetails.values().forEach(user -> System.out.printf("%s\t\t%s\t\t%s%n",
                user.getUserName(),
                user.getRole(),
                user.getDepartment()));
    }

    
    public void removeUser() {
        System.out.println("\nEnter the username of the user you want to remove:");
        String username = scanner.nextLine();

        // Check if the user exists
        Users selectedUser = LibraryData.userDetails.get(username);
        if (selectedUser == null) {
            System.out.println("Invalid username. Please try again.");
            return;
        }

        String deleteUserQuery = "DELETE FROM users WHERE username = ?";

        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement deleteUserStmt = conn.prepareStatement(deleteUserQuery)) {

            deleteUserStmt.setString(1, username);
            deleteUserStmt.executeUpdate();

            // Remove the user from the in-memory UserDetails map
            LibraryData.userDetails.remove(username);

            System.out.println("The user \"" + username + "\" has been removed successfully.");

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("An error occurred while removing the user. Please try again.");
        }
    }

    public void displayPendingBookRequests() {
        System.out.println("\nPending Book Requests:");
        System.out.println("ID\tTitle\t\tAuthor\t\tDepartment\tCopies Requested");
        System.out.println("--------------------------------------------------------------------------");

        String query = "SELECT request_id, title, author, department, copies_requested FROM book_requests WHERE status = 'Pending'";
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                System.out.printf("%d\t%s\t\t%s\t\t%s\t\t%d%n",
                        rs.getInt("request_id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getString("department"),
                        rs.getInt("copies_requested"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("An error occurred while fetching book requests.");
        }
    }


    
}
 
