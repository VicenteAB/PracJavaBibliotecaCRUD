package org.example.services;

import org.example.Libro;
import org.example.repository.LibroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class LibroService {

    @Autowired
    private LibroRepository libroRepository;

    public void guardarLibro(Libro libro) {
        libroRepository.guardarLibro(libro);
    }

    public Libro buscarPorISBN(String ISBN){
        return libroRepository.buscarPorISBN(ISBN);
    }

    public ArrayList<Libro> listarLibros(){
        return libroRepository.listarLibros();
    }

    public boolean eliminarLibro(String ISBN){
        return libroRepository.eliminarLibro(ISBN);
    }

}
