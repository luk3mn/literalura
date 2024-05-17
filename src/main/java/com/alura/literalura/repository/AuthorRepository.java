package com.alura.literalura.repository;

import com.alura.literalura.model.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AuthorRepository extends JpaRepository<Author, Long> {
    @Query("SELECT a FROM Author a WHERE a.deathYear > :year AND a.birthYear <= :year")
    List<Author> findAliveAuthors(String year);

    // TODO: 'Join' with book to get book languages
    @Query("SELECT a FROM Author a")
    List<Author> findByLanguage(String language);
}
