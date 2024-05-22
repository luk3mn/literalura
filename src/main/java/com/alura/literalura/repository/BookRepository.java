package com.alura.literalura.repository;

import com.alura.literalura.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {
    @Query("SELECT b FROM Book b WHERE b.language ILIKE %:language")
    List<Book> findByLanguage(String language);

    List<Book> findByLanguageContainingIgnoreCase(String language);

    @Query("SELECT b FROM Book b ORDER BY b.download DESC LIMIT 10")
    List<Book> getTopDownloaded();
}
