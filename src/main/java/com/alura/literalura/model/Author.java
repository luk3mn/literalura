package com.alura.literalura.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "authors")
public class Author {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(name = "birth_year")
    private String birthYear;

    @Column(name = "death_year")
    private String deathYear;

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Book> book;

    public Author() {
    }

    public List<Book> getBook() {
        return book;
    }

    public void setBook(List<Book> book) {
        // indicate that "Author" is owner of "Book"
        book.forEach(b -> b.setAuthor(this)); // to manage to save a new book and associate author_id to it. without it, it will save a new book without an author_id
        this.book = book;
    }

    public String getDeathYear() {
        return deathYear;
    }

    public void setDeathYear(String deathYear) {
        this.deathYear = deathYear;
    }

    public String getBirthYear() {
        return birthYear;
    }

    public void setBirthYear(String birthYear) {
        this.birthYear = birthYear;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return  "Autor: " + name + "\n" +
                "Ano de nasimento: " + birthYear + "\n" +
                "Ano de falecimento: " + deathYear + "\n" +
                "Titulo: " + book.stream().map(Book::getTitle).toList() + "\n";
    }
}
