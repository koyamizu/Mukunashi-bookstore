CREATE TABLE books(
  isbn CHAR(17) PRIMARY KEY
  ,title VARCHAR(100) NOT NULL
  ,price INT NOT NULL
  ,publish VARCHAR(20) NOT NULL
  ,published DATE NOT NULL
);

CREATE TABLE book_stocks(
  isbn CHAR(17) PRIMARY KEY
  ,stock_num INT NOT NULL
  ,CONSTRAINT fk_books_isbn FOREIGN KEY (isbn)
    REFERENCES books(isbn)
)

