package org.libreria.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.libreria.model.Usuario;
import org.libreria.util.Conexion;
import org.libreria.util.SecurityUtil;

public class UsuarioDAO {

    public Usuario autenticar(String username, String password) throws Exception {

        String sql = "SELECT id, username, rol, activo FROM usuarios "
                + "WHERE username = ? AND password_hash = ?";

        String passwordHash = SecurityUtil.hashSHA256Password(password);

        try (
            Connection con = Conexion.getInstancia().getConexion();
            PreparedStatement ps = con.prepareStatement(sql)
        ) {
            if (con == null) {
                throw new Exception("Error al conectar con la base de datos.");
            }

            ps.setString(1, username);
            ps.setString(2, passwordHash);

            try (ResultSet rs = ps.executeQuery()) {

                if (!rs.next()) {
                    throw new Exception("Usuario o contraseña incorrectos.");
                }

                if (!rs.getBoolean("activo")) {
                    throw new Exception("El usuario se encuentra desactivado.");
                }

                Usuario usuario = new Usuario();
                usuario.setId(rs.getInt("id"));
                usuario.setUsername(rs.getString("username"));
                usuario.setRol(rs.getString("rol"));
                usuario.setActivo(rs.getBoolean("activo"));

                return usuario;
            }

        } catch (SQLException e) {
            throw new Exception("Error en la base de datos: " + e.getMessage());
        }
    }

    public void registrarUsuario(
            String username,
            String password,
            String rol) throws Exception {

        String sql = "{call sp_registrar_usuario(?, ?, ?)}";

        String passwordHash =
                SecurityUtil.hashSHA256Password(password);

        try (
            Connection con = Conexion.getInstancia().getConexion();
            CallableStatement cs = con.prepareCall(sql)
        ) {

            if (con == null) {
                throw new Exception("Error al conectar con la base de datos.");
            }

            cs.setString(1, username);
            cs.setString(2, passwordHash);
            cs.setString(3, rol.toLowerCase());

            cs.execute();

        } catch (SQLException e) {

            if (e.getMessage() != null &&
                e.getMessage().contains("Duplicate entry")) {

                throw new Exception("El nombre de usuario ya existe.");
            }

            throw new Exception(
                    "Error en la base de datos: " + e.getMessage()
            );
        }
    }

    public List<Usuario> listarUsuarios() throws Exception {

        List<Usuario> usuarios = new ArrayList<>();

        String sql = "SELECT id, username, rol, activo "
                + "FROM usuarios ORDER BY id DESC";

        try (
            Connection con = Conexion.getInstancia().getConexion();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()
        ) {

            while (rs.next()) {

                Usuario usuario = new Usuario();

                usuario.setId(rs.getInt("id"));
                usuario.setUsername(rs.getString("username"));
                usuario.setRol(rs.getString("rol"));
                usuario.setActivo(rs.getBoolean("activo"));

                usuarios.add(usuario);
            }

        } catch (SQLException e) {
            throw new Exception(
                    "Error al consultar los usuarios: " + e.getMessage()
            );
        }

        return usuarios;
    }

    public void cambiarEstadoUsuario(int idUsuario, boolean activo)
            throws Exception {

        String sql = "UPDATE usuarios SET activo = ? WHERE id = ?";

        try (
            Connection con = Conexion.getInstancia().getConexion();
            PreparedStatement ps = con.prepareStatement(sql)
        ) {

            if (con == null) {
                throw new Exception("Error al conectar con la base de datos.");
            }

            ps.setBoolean(1, activo);
            ps.setInt(2, idUsuario);

            int filas = ps.executeUpdate();

            if (filas == 0) {
                throw new Exception("No se encontró el usuario.");
            }

        } catch (SQLException e) {
            throw new Exception(
                    "Error al cambiar el estado del usuario: "
                    + e.getMessage()
            );
        }
    }
}