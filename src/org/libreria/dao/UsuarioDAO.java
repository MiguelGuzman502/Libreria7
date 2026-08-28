package org.libreria.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.libreria.model.Usuario;
import org.libreria.util.Conexion;

public class UsuarioDAO {

    public Usuario autenticar(String username, String passwordHash) throws Exception {
        Usuario usuarioEncontrado = null;
        String sql = "{call sp_iniciar_sesion(?, ?)}";

        try (Connection con = Conexion.getInstancia().getConexion();
             CallableStatement cs = con.prepareCall(sql)) {

            if (con == null) {
                throw new Exception("Error al conectar con la base de datos.");
            }

            cs.setString(1, username);
            cs.setString(2, passwordHash);

            try (ResultSet rs = cs.executeQuery()) {
                if (rs.next()) {
                    usuarioEncontrado = new Usuario();
                    usuarioEncontrado.setId(rs.getInt("id"));
                    usuarioEncontrado.setUsername(rs.getString("username"));
                    usuarioEncontrado.setRol(rs.getString("rol"));
                } else {
                    throw new Exception("Usuario o contraseña incorrectos.");
                }
            }

        } catch (SQLException e) {
            System.err.println("Error en autenticar: " + e.getMessage());
            throw new Exception("Error en la base de datos: " + e.getMessage());
        }

        return usuarioEncontrado;
    }
}