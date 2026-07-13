package org.example.controllers;

import org.example.Libro;
import org.example.services.LibroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/libros")
public class LibroController {

    @Autowired
    private LibroService libroService;

    @PostMapping
    public void guardarLibro(@RequestBody Libro libro) {
        libroService.guardarLibro(libro);
    }

    @GetMapping("/{isbn}")
    public Libro buscarPorISBN (@PathVariable String isbn){
        return libroService.buscarPorISBN(isbn);
    }

    @GetMapping()
    public List<Libro> listarLibros(){
        return libroService.listarLibros();
    }

    @DeleteMapping("/{isbn}")
    public boolean eliminarLibro(@PathVariable String isbn){
        return libroService.eliminarLibro(isbn);
    }
}
