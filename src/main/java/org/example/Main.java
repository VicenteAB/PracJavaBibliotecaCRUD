package org.example;
import org.example.repository.LibroRepository;

import java.sql.Connection;
import java.util.HashMap;

public class Main{
    public static void main(String[] args){

        Libro libro1 = new Libro("978-123", "El Hobbit", "Tolkien");
        Libro libro2 = new Libro("978-456", "Harry Potter", "J. K. Rowling");
        Libro libro3 = new Libro("978-789", "Don Quijote", "Cervantes");

        LibroRepository l1 = new LibroRepository();

        l1.guardarLibro(libro1);
        l1.guardarLibro(libro2);
        l1.guardarLibro(libro3);

        Libro encontrado = l1.buscarPorISBN("978-123");
        System.out.println("!Libro encontrado: " + encontrado.getTitulo());

        l1.listarLibros();

        l1.eliminarLibro("978-123");

    }
}