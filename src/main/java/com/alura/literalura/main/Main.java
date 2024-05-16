package com.alura.literalura.main;

import com.alura.literalura.model.BookData;
import com.alura.literalura.service.ConvertData;
import com.alura.literalura.service.ExtractData;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.Scanner;

public class Main {

    private final Scanner scanner = new Scanner(System.in);
    private final ExtractData extractData = new ExtractData();
    private final ConvertData convert = new ConvertData();

    public void showOptions() {

        while (true) {
            System.out.println("""

            *************** OPÇÕES *****************
            [1] Buscar livro pelo titulo
            [2] Listar livros registrados
            [3] Listar autores registrados
            [4] Listar autores vivos em um determinado ano
            [5] Listar livros em um determinado idiooma
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
                    System.out.println("2");
                    break;
                case "3":
                    System.out.println("3");
                    break;
                case "4":
                    System.out.println("4");
                    break;
                case "5":
                    System.out.println("5");
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

        var newTitle = title.replace(" ", "%20").toLowerCase();
        var jsonResponse = extractData.getPageFrom(newTitle);

        try {
            var json = convert.toObject(jsonResponse);
//            System.out.println(json.get("results").get(0));

            BookData book = convert.getData(String.valueOf(json.get("results").get(0)), BookData.class);
            System.out.println("--------------------------------------");
            System.out.println("--------  RESULTADO DA BUSCA  --------");
            System.out.println("--------------------------------------");
            System.out.println("Titulo: " + book.title());
            book.author().forEach(a -> System.out.println("Autor: " + a.name()));
            System.out.println("Idioma: " + book.language().get(0));
            System.out.println("Numero de Downloads: " + book.download());
            System.out.println("--------------------------------------");

            //TODO: Store data on database
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }
}
