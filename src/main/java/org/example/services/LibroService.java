package org.example.services;

import org.example.model.Libro;
import org.example.repository.LibroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LibroService {

    @Autowired
    private LibroRepository libroRepository;

    public boolean guardarLibro(Libro libro) {

        if(libroRepository.existsById(libro.getISBN())){
            return false;
        }else{
            libroRepository.save(libro);
            return true;
        }
    }

    public Libro buscarPorISBN(String ISBN){
        return libroRepository.findById(ISBN).orElse(null);
    }

    public List<Libro> listarLibros(){
        return libroRepository.findAll();
    }

    public boolean actualizarLibro(String ISBN, Libro libro){

        if(libroRepository.existsById(ISBN)){

            libro.setISBN(ISBN);
            libroRepository.save(libro);

            return true;

        }else{
            return false;

        }
    }

    public boolean eliminarLibro(String ISBN){

        if(libroRepository.existsById(ISBN)){
            libroRepository.deleteById(ISBN);
            return true;
        }
        return false;
    }

}
