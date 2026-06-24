package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DataBaseConnection {

    public static Connection conectar() {

        Connection conexion = null;

        try {
            Properties props = new Properties();
            props.load(DataBaseConnection.class.getClassLoader().getResourceAsStream("config.properties"));

            // Configuración para base de datos
            String URL = props.getProperty("db.url");
            String USUARIO = props.getProperty("db.usuario");
            String CONTRASENA = props.getProperty("db.contrasena");

            // Cargar el driver y establecer la conexión
            conexion = DriverManager.getConnection(URL, USUARIO, CONTRASENA);
            System.out.println("¡Conexión exitosa a la base de datos!");

        } catch (Exception e) {
            System.out.println("Error de conexión: " + e.getMessage());
        }
        return conexion;
    }
}
