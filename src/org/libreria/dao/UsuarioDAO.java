package org.libreria.dao;

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

    // 1. AUTENTICACIÓN Y VALIDACIÓN DE LOGIN
    public Usuario autenticar(String username, String passwordIngresada) throws Exception {
        String sql = "SELECT * FROM usuario WHERE username = ?";
        
        try (Connection conn = Conexion.getInstancia().getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    boolean activo = rs.getBoolean("activo");
                    if (!activo) {
                        throw new Exception("El usuario se encuentra inactivo.");
                    }

                    String hashBD = rs.getString("password");
                    if (SecurityUtil.verifyPassword(passwordIngresada, hashBD)) {
                        Usuario u = new Usuario();
                        u.setId(rs.getInt("id"));
                        u.setUsrname(rs.getString("username"));
                        u.setPassword(hashBD);
                        u.setRol(rs.getString("rol"));
                        u.setActivo(activo);
                        return u;
                    } else {
                        throw new Exception("Contraseña incorrecta.");
                    }
                } else {
                    throw new Exception("El usuario no existe.");
                }
            }
        }
    }

    // 2. CREAR / ALTA DE USUARIO
    public boolean crearUsuario(Usuario u) throws SQLException {
        String sql = "INSERT INTO usuario (username, password, rol, activo) VALUES (?, ?, ?, ?)";
        try (Connection conn = Conexion.getInstancia().getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, u.getUsrname());
            ps.setString(2, SecurityUtil.hashPassword(u.getPassword()));
            ps.setString(3, u.getRol());
            ps.setBoolean(4, true);
            
            return ps.executeUpdate() > 0;
        }
    }

    // 3. OBTENER LISTADO DE USUARIOS (VISTA LISTADO)
    public List<Usuario> listarUsuarios() throws SQLException {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT id, username, rol, activo FROM usuario";
        
        try (Connection conn = Conexion.getInstancia().getConexion();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                Usuario u = new Usuario();
                u.setId(rs.getInt("id"));
                u.setUsrname(rs.getString("username"));
                u.setRol(rs.getString("rol"));
                u.setActivo(rs.getBoolean("activo"));
                lista.add(u);
            }
        }
        return lista;
    }

    // 4. EDICIÓN DE USUARIO (DATOS BÁSICOS)
    public boolean actualizarUsuario(Usuario u) throws SQLException {
        String sql = "UPDATE usuario SET username = ?, rol = ? WHERE id = ?";
        try (Connection conn = Conexion.getInstancia().getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, u.getUsrname());
            ps.setString(2, u.getRol());
            ps.setInt(3, u.getId());
            
            return ps.executeUpdate() > 0;
        }
    }

    // 5. DESACTIVACIÓN / ESTADO DE USUARIO
    public boolean cambiarEstadoUsuario(int idUsuario, boolean activo) throws SQLException {
        String sql = "UPDATE usuario SET activo = ? WHERE id = ?";
        try (Connection conn = Conexion.getInstancia().getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setBoolean(1, activo);
            ps.setInt(2, idUsuario);
            
            return ps.executeUpdate() > 0;
        }
    }

    // 6. CAMBIO DE CONTRASEÑA (ACTUALIZAR HASH)
    public boolean cambiarPassword(int idUsuario, String passActual, String passNueva) throws Exception {
        String sqlSelect = "SELECT password FROM usuario WHERE id = ?";
        String sqlUpdate = "UPDATE usuario SET password = ? WHERE id = ?";
        
        try (Connection conn = Conexion.getInstancia().getConexion()) {
            // Verificar password actual
            try (PreparedStatement psSelect = conn.prepareStatement(sqlSelect)) {
                psSelect.setInt(1, idUsuario);
                try (ResultSet rs = psSelect.executeQuery()) {
                    if (rs.next()) {
                        String hashActual = rs.getString("password");
                        if (!SecurityUtil.verifyPassword(passActual, hashActual)) {
                            throw new Exception("La contraseña actual no es correcta.");
                        }
                    }
                }
            }

            // Actualizar a la nueva contraseña
            try (PreparedStatement psUpdate = conn.prepareStatement(sqlUpdate)) {
                psUpdate.setString(1, SecurityUtil.hashPassword(passNueva));
                psUpdate.setInt(2, idUsuario);
                return psUpdate.executeUpdate() > 0;
            }
        }
    }
}