CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       username VARCHAR(50) NOT NULL UNIQUE,
                       password VARCHAR(255) NOT NULL,
                       email VARCHAR(255) NOT NULL UNIQUE,
                       first_name VARCHAR(50),
                       last_name VARCHAR(50),
                       role VARCHAR(20) NOT NULL
);

CREATE TABLE books (
                       id BIGSERIAL PRIMARY KEY,
                       title VARCHAR(255) NOT NULL,
                       author VARCHAR(255) NOT NULL,
                       publication_year INTEGER,
                       genre VARCHAR(50),
                       available_copies INTEGER NOT NULL,
                       total_copies INTEGER NOT NULL,
                       book_type VARCHAR(20) NOT NULL DEFAULT 'PHYSICAL'
);

CREATE TABLE borrowings (
                            id BIGSERIAL PRIMARY KEY,
                            user_id BIGINT NOT NULL,
                            book_id BIGINT NOT NULL,
                            status VARCHAR(20) NOT NULL,
                            borrow_date DATE NOT NULL,
                            due_date DATE NOT NULL,
                            return_date DATE,
                            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                            FOREIGN KEY (user_id) REFERENCES users(id),
                            FOREIGN KEY (book_id) REFERENCES books(id)
);

CREATE INDEX idx_borrowings_user_id ON borrowings(user_id);
CREATE INDEX idx_borrowings_book_id ON borrowings(book_id);
CREATE INDEX idx_borrowings_status ON borrowings(status);
CREATE INDEX idx_books_title ON books(title);
CREATE INDEX idx_books_author ON books(author);
CREATE INDEX idx_books_book_type ON books(book_type);
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);

INSERT INTO users (username, password, email, first_name, last_name, role)
VALUES (
           'admin',
           '$2a$12$0m7puby1aJ7hFC2i0yb4euV7dofmH30zokXeOsDWLoAI03ipuZE9S',
           'admin@bookstore.com',
           'Admin',
           'Administrator',
           'ADMIN'
       );

INSERT INTO users (username, password, email, first_name, last_name, role)
VALUES (
           'user',
           '$2a$12$0m7puby1aJ7hFC2i0yb4euV7dofmH30zokXeOsDWLoAI03ipuZE9S',
           'user@bookstore.com',
           'Test',
           'User',
           'USER'
       );

-- Insert some sample books for testing
INSERT INTO books (title, author, publication_year, genre, available_copies, total_copies, book_type)
VALUES
    ('The Great Gatsby', 'F. Scott Fitzgerald', 1925, 'Fiction', 3, 3, 'PHYSICAL'),
    ('To Kill a Mockingbird', 'Harper Lee', 1960, 'Fiction', 2, 2, 'PHYSICAL'),
    ('1984', 'George Orwell', 1949, 'Dystopian Fiction', 1, 1, 'EBOOK'),
    ('Pride and Prejudice', 'Jane Austen', 1813, 'Romance', 2, 2, 'AUDIOBOOK');