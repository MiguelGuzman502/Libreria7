package org.libreria.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.libreria.model.Usuario;
import org.libreria.util.Conexion;
import org.libreria.util.SecurityUtil;

public class UsuarioDAO {

    public Usuario autenticar(
            String username,
            String password) throws Exception {

        Usuario usuarioEncontrado = null;

        String sql = "{call sp_iniciar_sesion(?, ?)}";

        String passwordHash =
                SecurityUtil.hashSHA256Password(password);

        try (
            Connection con =
                    Conexion.getInstancia().getConexion()
        ) {

            if (con == null) {
                throw new Exception(
                    "Error al conectar con la base de datos."
                );
            }

            try (
                CallableStatement cs =
                        con.prepareCall(sql)
            ) {

                cs.setString(1, username);
                cs.setString(2, passwordHash);

                try (
                    ResultSet rs = cs.executeQuery()
                ) {

                    if (rs.next()) {

                        usuarioEncontrado = new Usuario();

                        usuarioEncontrado.setId(
                            rs.getInt("id")
                        );

                        usuarioEncontrado.setUsername(
                            rs.getString("username")
                        );

                        usuarioEncontrado.setRol(
                            rs.getString("rol")
                        );

                    } else {

                        throw new Exception(
                            "Usuario o contraseña incorrectos."
                        );
                    }
                }
            }

        } catch (SQLException e) {

            System.err.println(
                "Error en autenticar: "
                + e.getMessage()
            );

            throw new Exception(
                "Error en la base de datos: "
                + e.getMessage()
            );
        }

        return usuarioEncontrado;
    }


    // =====================================================
    // REGISTRAR USUARIO
    // =====================================================

    public void registrarUsuario(
            String username,
            String password,
            String rol) throws Exception {

        String sql =
                "{call sp_registrar_usuario(?, ?, ?)}";

        String passwordHash =
                SecurityUtil.hashSHA256Password(password);

        try (
            Connection con =
                    Conexion.getInstancia().getConexion()
        ) {

            if (con == null) {
                throw new Exception(
                    "Error al conectar con la base de datos."
                );
            }

            try (
                CallableStatement cs =
                        con.prepareCall(sql)
            ) {

                cs.setString(1, username);
                cs.setString(2, passwordHash);
                cs.setString(3, rol.toLowerCase());

                cs.execute();
            }

        } catch (SQLException e) {

            if (e.getMessage() != null &&
                e.getMessage().contains("Duplicate entry")) {

                throw new Exception(
                    "El nombre de usuario ya existe."
                );
            }

            System.err.println(
                "Error al registrar usuario: "
                + e.getMessage()
            );

            throw new Exception(
                "Error en la base de datos: "
                + e.getMessage()
            );
        }
    }


    // =====================================================
    // LISTAR USUARIOS
    // =====================================================

    public List<Usuario> listarUsuarios() throws Exception {

        List<Usuario> usuarios =
                new ArrayList<>();

        String sql =
                "SELECT id, username, rol, activo "
                + "FROM usuarios "
                + "ORDER BY id DESC";

        try (
            Connection con =
                    Conexion.getInstancia().getConexion();

            java.sql.PreparedStatement ps =
                    con.prepareStatement(sql);

            ResultSet rs =
                    ps.executeQuery()
        ) {

            while (rs.next()) {

                Usuario usuario =
                        new Usuario();

                usuario.setId(
                        rs.getInt("id")
                );

                usuario.setUsername(
                        rs.getString("username")
                );

                usuario.setRol(
                        rs.getString("rol")
                );

                usuario.setActivo(
                        rs.getBoolean("activo")
                );

                usuarios.add(usuario);
            }

        } catch (SQLException e) {

            System.err.println(
                "Error al listar usuarios: "
                + e.getMessage()
            );

            throw new Exception(
                "Error al consultar los usuarios: "
                + e.getMessage()
            );
        }

        return usuarios;
    }
}