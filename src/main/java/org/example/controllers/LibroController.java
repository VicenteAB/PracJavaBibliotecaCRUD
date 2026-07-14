package org.example.controllers;

import org.example.Libro;
import org.example.services.LibroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/libros")
public class LibroController {

    @Autowired
    private LibroService libroService;

    @PostMapping
    public ResponseEntity<Libro> guardarLibro(@RequestBody Libro libro) {

        if(libroService.guardarLibro(libro)){
            return ResponseEntity.status(201).body(libro);
        }else{
            return ResponseEntity.status(409).build();
        }

    }

    @GetMapping("/{isbn}")
    public ResponseEntity<Libro> buscarPorISBN (@PathVariable String isbn){

        Libro libro = libroService.buscarPorISBN(isbn);

        if(libro!= null){
            return ResponseEntity.ok(libro);
        }else{
            return ResponseEntity.notFound().build();
        }

    }

    @GetMapping()
    public ResponseEntity<List> listarLibros(){

        List<Libro> libro = libroService.listarLibros();

        return ResponseEntity.ok(libro);
    }

    @DeleteMapping("/{isbn}")
    public ResponseEntity<Boolean> eliminarLibro(@PathVariable String isbn){

        if(libroService.eliminarLibro(isbn)){
            return ResponseEntity.ok(true);
        }else{
            return ResponseEntity.notFound().build();
        }

    }
}
