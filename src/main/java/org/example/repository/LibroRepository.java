package org.example.repository;

import org.example.DataBaseConnection;
import org.example.Libro;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LibroRepository {

    // Se obtiene la conexión a la base de datos al crear el repositorio.
    // DataBaseConnection.conectar() establece y retorna la conexión usando JDBC.
    Connection conexion = DataBaseConnection.conectar();

    // =====================================================================
    // GUARDAR LIBRO - INSERT
    // =====================================================================
    // PreparedStatement permite preparar una query con "huecos" marcados con ?
    // en vez de concatenar strings directamente (lo cual es inseguro).
    // Ventajas:
    //   - Evita SQL Injection (ataques maliciosos al inyectar código SQL)
    //   - Es más legible y seguro que concatenar strings
    // =====================================================================
    public void guardarLibro(Libro libro) {

        try {
            // 1. Se prepara la query con ? en lugar de los valores reales.
            //    Cada ? representa un parámetro que se seteará después.
            PreparedStatement pstmt = conexion.prepareStatement(
                    "INSERT INTO libros VALUES (?, ?, ?)"
            );

            // 2. Se reemplazan los ? por los valores reales.
            //    El primer parámetro (int) indica la posición del ?:
            //      setString(1, ...) → primer  ?  → ISBN
            //      setString(2, ...) → segundo ?  → titulo
            //      setString(3, ...) → tercer  ?  → autor
            pstmt.setString(1, libro.getISBN());
            pstmt.setString(2, libro.getTitulo());
            pstmt.setString(3, libro.getAutor());

            // 3. Se ejecuta la query.
            //    executeUpdate() se usa para INSERT, UPDATE y DELETE.
            //    Retorna un int con la cantidad de filas afectadas.
            //    (no usamos executeQuery() porque eso es solo para SELECT)
            pstmt.executeUpdate();

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    // =====================================================================
    // BUSCAR POR ISBN - SELECT
    // =====================================================================
    // ResultSet es el objeto que contiene los resultados de un SELECT.
    // Funciona como una tabla de filas; hay que recorrerla con rs.next()
    // para acceder a cada fila de resultados.
    // =====================================================================
    public Libro buscarPorISBN(String ISBN) {

        // Se inicializa en null. Si no se encuentra el libro, se retorna null,
        // lo cual indica claramente "no encontré nada".
        Libro libroEncontrado = null;

        try {
            // 1. Se prepara el SELECT con ? para el valor del ISBN.
            PreparedStatement pstmt = conexion.prepareStatement(
                    "SELECT * FROM libros WHERE ISBN = ?"
            );

            // 2. Se setea el valor del ? con el ISBN recibido como parámetro.
            pstmt.setString(1, ISBN);

            // 3. Se ejecuta la query con executeQuery() (exclusivo para SELECT).
            //    Retorna un ResultSet con las filas encontradas.
            ResultSet rs = pstmt.executeQuery();

            // 4. rs.next() avanza al siguiente registro y retorna true si existe.
            //    Como buscamos por ISBN (único), esperamos máximo una fila.
            if (rs.next()) {
                // 5. Se extraen los valores de cada columna con rs.getString("columna").
                //    El nombre de la columna debe coincidir con el de la tabla en MySQL.
                String isbn  = rs.getString("ISBN");
                String titulo = rs.getString("titulo");
                String autor  = rs.getString("autor");

                // 6. Con los datos extraídos se crea el objeto Libro.
                libroEncontrado = new Libro(isbn, titulo, autor);
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return libroEncontrado;
    }

    // Pendiente de implementar
    public void listarLibros() {

        try{
            PreparedStatement pstmt = conexion.prepareStatement("SELECT * FROM libros");
            ResultSet rs = pstmt.executeQuery();

            while(rs.next()){
                String isbn  = rs.getString("ISBN");
                String titulo = rs.getString("titulo");
                String autor  = rs.getString("autor");

                System.out.println(isbn + " | " + titulo + " | " + autor);
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    // Pendiente de implementar
    public boolean eliminarLibro(String ISBN) {

        try{
            PreparedStatement pstmt = conexion.prepareStatement("DELETE FROM libros WHERE ISBN = ?");
            pstmt.setString(1, ISBN);

            pstmt.executeUpdate();

            return true;

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return false;
    }
}