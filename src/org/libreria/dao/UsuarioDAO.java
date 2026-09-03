package org.libreria.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.libreria.model.Usuario;
import org.libreria.util.Conexion;
import org.libreria.util.SecurityUtil;

public class UsuarioDAO {

    public Usuario autenticar(String username, String password) throws Exception {
        String passwordHash = SecurityUtil.hashSHA256Password(password);
        String sql = "{call sp_iniciar_sesion(?, ?)}";

        try (Connection con = Conexion.getInstancia().conectar();
             CallableStatement cs = con.prepareCall(sql)) {

            cs.setString(1, username);
            cs.setString(2, passwordHash);

            try (ResultSet rs = cs.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }

                Usuario usuario = new Usuario();
                usuario.setId(rs.getInt("id"));
                usuario.setUsername(rs.getString("username"));
                usuario.setRol(rs.getString("rol"));
                return usuario;
            }
        } catch (SQLException e) {
            System.err.println("Error en autenticar: " + e.getMessage());
            throw new Exception("No se pudo conectar con la base de datos. " + e.getMessage(), e);
        }
    }
}
