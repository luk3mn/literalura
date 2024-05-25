package com.alura.literalura.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "languages")
public class Language {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String abbreviation;

    @ManyToMany(mappedBy = "language")
    private List<Book> books;

    public Language() {
    }

    public Language(String language) {
        this.abbreviation = language;
    }

    public String getAbbreviation() {
        return abbreviation;
    }
}
