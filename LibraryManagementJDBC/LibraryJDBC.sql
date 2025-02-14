create database Library;
use library;

-- Users Table
CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role ENUM('Student', 'Professor', 'Librarian') NOT NULL,
    department VARCHAR(100) -- Assuming department is a string field in your Users class
);

-- Books Table
CREATE TABLE books (
    book_id INT AUTO_INCREMENT PRIMARY KEY,
    book_name VARCHAR(255) NOT NULL,
    author VARCHAR(255) NOT NULL,
    department VARCHAR(100),
    total_copies INT NOT NULL,
    available_copies INT NOT NULL,
    CHECK (total_copies >= 0),
    CHECK (available_copies >= 0 AND available_copies <= total_copies)
);


-- Checkouts Table
CREATE TABLE checkouts (
    checkout_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    book_id INT NOT NULL,
    checkout_date DATE NOT NULL,
    due_date DATE NOT NULL,
    return_date DATE,
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (book_id) REFERENCES books(book_id),
    UNIQUE (user_id, book_id, checkout_date)
);

-- Book Requests Table
CREATE TABLE book_requests (
    request_id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(255),
    department VARCHAR(100), -- Assuming department is a string field relevant to the book request
    copies_requested INT DEFAULT 1 CHECK (copies_requested > 0),
    requested_by INT NOT NULL,
    request_date DATE DEFAULT (CURRENT_DATE),
    FOREIGN KEY (requested_by) REFERENCES users(user_id)
);

-- Electronics and Communication Engineering (ECE)
INSERT INTO books (book_name, author, department, total_copies, available_copies) VALUES
('The Art of Electronics', 'Paul Horowitz and Winfield Hill', 'ECE', 10, 10),
('Microelectronic Circuits', 'Adel S. Sedra and Kenneth C. Smith', 'ECE', 8, 8),
('Digital Signal Processing', 'John G. Proakis and Dimitris G. Manolakis', 'ECE', 7, 7);

-- Electrical and Electronics Engineering (EEE)
INSERT INTO books (book_name, author, department, total_copies, available_copies) VALUES
('Electrical Machinery', 'P.S. Bimbhra', 'EEE', 5, 5),
('Power System Engineering', 'I.J. Nagrath and D.P. Kothari', 'EEE', 6, 6),
('Control Systems Engineering', 'Norman S. Nise', 'EEE', 4, 4);

-- Mechanical Engineering (MEC)
INSERT INTO books (book_name, author, department, total_copies, available_copies) VALUES
('Engineering Mechanics: Dynamics', 'J.L. Meriam and L.G. Kraige', 'MEC', 9, 9),
('Mechanical Engineering Design', 'J.E. Shigley and C.R. Mischke', 'MEC', 7, 7),
('Thermodynamics: An Engineering Approach', 'Yunus A. Çengel and Michael A. Boles', 'MEC', 8, 8);

-- Computer Science Engineering (CSE)
INSERT INTO books (book_name, author, department, total_copies, available_copies) VALUES
('Introduction to Algorithms', 'Thomas H. Cormen, Charles E. Leiserson, Ronald L. Rivest, and Clifford Stein', 'CSE', 10, 10),
('Operating System Concepts', 'Abraham Silberschatz, Peter B. Galvin, and Greg Gagne', 'CSE', 7, 7),
('Computer Networks', 'Andrew S. Tanenbaum and David J. Wetherall', 'CSE', 6, 6);

-- Inserting Librarian Data into Users Table
INSERT INTO users (username, password, role, department) VALUES
('lib_john', 'libpass123', 'Librarian', 'ECE'),
('lib_emma', 'libpass456', 'Librarian', 'EEE'),
('lib_li', 'libpass789', 'Librarian', 'MEC'),
('lib_olivia', 'libpass101', 'Librarian', 'CSE');

ALTER TABLE book_requests ADD COLUMN status ENUM('Pending', 'Fulfilled', 'Declined') DEFAULT 'Pending';
