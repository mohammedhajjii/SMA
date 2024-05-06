package com.example.sma.modelsandrepositories;

import com.example.sma.modelsandrepositories.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {
    Book findByBookNameAndSellerName(String bookName, String sellerName);
}
