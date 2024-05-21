package com.alura.literalura.main;

import com.alura.literalura.model.*;
import com.alura.literalura.repository.AuthorRepository;
import com.alura.literalura.repository.BookRepository;
import com.alura.literalura.service.ConvertData;
import com.alura.literalura.service.ExtractData;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;

    private final Scanner scanner = new Scanner(System.in);
    private final ExtractData extractData = new ExtractData();
    private final ConvertData convert = new ConvertData();

    public Main(BookRepository bookRepository, AuthorRepository authorRepository) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
    }

    public void showOptions() {

        while (true) {
            System.out.println("""

            *************** OPÇÕES *****************
            [1] Buscar livro pelo titulo
            [2] Listar livros registrados
            [3] Listar autores registrados
            [4] Listar autores vivos em um determinado ano
            [5] Listar livros em um determinado idioma
            [0] Sair

            Escolha um opção:
            """);
            var option = scanner.nextLine();

            if (option.equalsIgnoreCase("0")) {
                System.out.println("""

                ***************************
                *** APLICAÇÃO ENCERRADA ***
                ***************************
                """);
                break;
            }

            switch (option) {
                case "1":
                    getData();
                    break;
                case "2":
                    getBooks();
                    break;
                case "3":
                    getAuthors();
                    break;
                case "4":
                    getAuthorByTimeline();
                    break;
                case "5":
                    getAuthorByLanguage();
                    break;
                default:
                    System.out.println("""
                    **********************
                    *** OPÇÃO INVÁLIDA ***
                    **********************
                    """);

            }
        }
    }

    private void getData() {
        System.out.println("Informe o título do livro: ");
        var title = scanner.nextLine();

        var jsonResponse = extractData.getPageFrom(title);
//        var jsonResponse = extractData.getPageFrom("Argentina, Legend and History");
        try {
            var json = convert.toObject(jsonResponse);
//            System.out.println(json.get("results").get(0));

            BookData data = convert.getData(String.valueOf(json.get("results").get(0)), BookData.class);
            System.out.println("--------------------------------------");
            System.out.println("--------  RESULTADO DA BUSCA  --------");
            System.out.println("--------------------------------------");
            System.out.println("Titulo: " + data.title());
            data.author().forEach(a -> System.out.println("Autor: " + a.name()));
            System.out.println("Idioma: " + data.language());
            System.out.println("Numero de Downloads: " + data.download());
            System.out.println("--------------------------------------");

            //TODO: Store data on database
            // * One book to Many Authors: "Argentina, Legend and History"

//            Language language = Language.fromInput("en");

//            for (AuthorData d : data.author()) {
//                System.out.println(d);
//            }

//            System.out.println(language);


//            Book newBook = new Book(book.title(), book.author().get(), book.language(), book.download());



            Author author = new Author();
            for (AuthorData dAuthor : data.author()) {
                author.setName(dAuthor.name());
                author.setBirthYear(dAuthor.birthYear());
                author.setDeathYear(dAuthor.deathYear());
            }



//            authorRepository.save(author);

//            author.setName(String.valueOf(data.author().get(0)));
//            author.setBirthYear(data.author());
//            Language language = new Language();
//            toLanguage(data);
//            for (String dLanguage : data.language()) {
//                language.setLanguage(dLanguage);
//                System.out.println(dLanguage);
//            }
//            language.setLanguage(data.language());

            Book book = new Book(data.title(), author, data.language().get(0), data.download());
//            book.setTitle(data.title());
//            book.setAuthor(author);
//            book.setLanguage();
            bookRepository.save(book);

            System.out.println(book);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }

//    private List<Language> toLanguage(BookData data) {
//        List<Language> languages = new ArrayList<>();
//        for (String l : data.language()) {
//            System.out.println(l);
//        }
//
//        return languages;
//    }

    private void getBooks() {
        List<Book> books = bookRepository.findAll();
        books.forEach(System.out::println);
//        System.out.println();
    }

    private void getAuthors() {
        List<Author> authors = authorRepository.findAll();

        System.out.println("""
                ----------------------------
                --------- Autores ----------
                ----------------------------
                """);
        authors.forEach(System.out::println);
    }

    private void getAuthorByTimeline() {
        System.out.println("Insira o ano em que deseja pesquisar: ");
        var year = scanner.nextLine();

        List<Author> aliveAuthors = authorRepository.findAliveAuthors(year);

        System.out.println(
                """
                -------------------------------
                """ +
                "---- Autores vivos em " + year + " ----\n" +
                """
                -------------------------------
                """
        );
        aliveAuthors.forEach(System.out::println);
    }

    private void getAuthorByLanguage() {
        System.out.println("""
        -----------------------------------------
        --------------- IDIOMAS -----------------
        -----------------------------------------
        [ES] Espanhol
        [EN] Inglês
        [FR] Francês
        [PT] Português
        
        Insira o idioma para realizar a busca:
        """);
        var language = scanner.nextLine().toLowerCase();

        List<Book> books = bookRepository.findByLanguage(language);
        System.out.println(
                """
                -----------------------
                """ +
                        "---- Livros em " + language.toUpperCase() + " ----\n" +
                        """
                        -----------------------
                        """
        );
        books.forEach(System.out::println);
    }

}
