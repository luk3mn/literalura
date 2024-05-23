package com.alura.literalura.main;

import com.alura.literalura.model.Author;
import com.alura.literalura.model.AuthorData;
import com.alura.literalura.model.Book;
import com.alura.literalura.model.BookData;
import com.alura.literalura.repository.AuthorRepository;
import com.alura.literalura.repository.BookRepository;
import com.alura.literalura.service.ConvertData;
import com.alura.literalura.service.ExtractData;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
            [6] Listar os 10 livros mais baixados
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
                case "6":
                    getTopDownloaded();
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
        try {
            var json = convert.toObject(jsonResponse);

            // TODO: avoid exception if anyone book had been found (show a friendly message)
            BookData data = convert.getData(String.valueOf(json.get("results").get(0)), BookData.class);
            System.out.println("--------------------------------------");
            System.out.println("--------  RESULTADO DA BUSCA  --------");
            System.out.println("--------------------------------------");
            System.out.println("Titulo: " + data.title());
            data.author().forEach(a -> System.out.println("Autor: " + a.name()));
            System.out.println("Idioma: " + data.language().get(0));
            System.out.println("Numero de Downloads: " + data.download());
            System.out.println("--------------------------------------");

            saveData(data);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } catch (DataIntegrityViolationException e) {
            System.out.println("""
            ************************************************
            *** O livro ja foi cadastrado anteriormente! ***
            ************************************************
            """);
        }

    }

    private void saveData(BookData data) {
        Optional<Author> storedAuthor = authorRepository.findByName(data.author().get(0).name());


        List<Book> bookCollection = new ArrayList<>();

        if (storedAuthor.isPresent()) {
            Book book = new Book();
            var author = storedAuthor.get();

            book.setTitle(data.title());
            book.setLanguage(data.language().get(0));
            book.setDownload(data.download());

            // create a book collection to set on author
            bookCollection.add(book);
            author.setBook(bookCollection);

            authorRepository.save(author);
        } else {

            Author author = new Author();
            for (AuthorData dAuthor : data.author()) {
                author.setName(dAuthor.name());
                author.setBirthYear(dAuthor.birthYear());
                author.setDeathYear(dAuthor.deathYear());
            }

            Book book = new Book(data.title(), author, data.language().get(0), data.download());
            bookRepository.save(book);
        }
    }

    private void getBooks() {
        List<Book> books = bookRepository.findAll();
        books.forEach(System.out::println);
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

        List<Book> books = bookRepository.findByLanguageContainingIgnoreCase(language);
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

    private void getTopDownloaded() {
        List<Book> books = bookRepository.getTopDownloaded();
        System.out.println(
        """
        -----------------------------
        -- TOP 10 DOWNLOADED BOOKS --
        -----------------------------
        """
        );
        books.forEach(System.out::println);
    }

}
