package org.libreria.util;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
 
public class Conexion {
    private static Conexion instancia;
    // Configuración del string de conexión, y credenciales
    private static final String URL = "jdbc:mysql://localhost:3306/libreriadb_in4cm?serverTimezone=UTC";
    private static final String USER = "IN4CM";
    private static final String PASSWORD = "#NdimAM4";
    //Constructor privado para evitar que hagan "new Conexion()" fuera de esta clase
    private Conexion() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("Error Driver: " + e.getMessage());
        }
    }
 
    //Método público estático para obtener la única instancia del Gestor
    public static synchronized Conexion getInstancia() {
        if (instancia == null) {
            instancia = new Conexion();
        }
        return instancia;
    }
 
    //Método para entregar una conexión fresca cada vez que se pida
    public Connection conectar() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

}
