package org.libreria.dao.impl;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import org.libreria.dao.LibroDAO;
import org.libreria.model.Libros;
import org.libreria.util.Conexion;

public class LibroDAOImpl implements LibroDAO {

    private Libros mapear(ResultSet rs) throws SQLException {

        Libros libro = new Libros();

        libro.setIsbn(rs.getString("isbn"));
        libro.setTitulo(rs.getString("titulo"));

        Date fecha = rs.getDate("fecha_publicacion");

        if (fecha != null) {
            libro.setFechaPublicacion(fecha.toLocalDate());
        }

        libro.setPrecio(rs.getBigDecimal("precio"));
        libro.setIdCategoria(rs.getInt("id_categoria"));
        libro.setNitEditorial(rs.getString("nit_editorial"));
        libro.setStock(rs.getInt("stock"));

        return libro;
    }

    @Override
    public void insertar(Libros libro) throws Exception {

        String sql = "INSERT INTO libros "
                + "(isbn, titulo, fecha_publicacion, precio, "
                + "id_categoria, nit_editorial, stock) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conexion = Conexion.getInstancia().getConexion();
                PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, libro.getIsbn());
            ps.setString(2, libro.getTitulo());

            if (libro.getFechaPublicacion() == null) {
                ps.setNull(3, Types.DATE);
            } else {
                ps.setDate(3, Date.valueOf(libro.getFechaPublicacion()));
            }

            ps.setBigDecimal(4, libro.getPrecio());
            ps.setInt(5, libro.getIdCategoria());
            ps.setString(6, libro.getNitEditorial());
            ps.setInt(7, libro.getStock());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new Exception("Error al insertar libro: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Libros> listar() throws Exception {

        List<Libros> lista = new ArrayList<>();

        String sql = "SELECT isbn, titulo, fecha_publicacion, precio, "
                + "id_categoria, nit_editorial, stock "
                + "FROM libros ORDER BY titulo";

        try (Connection conexion = Conexion.getInstancia().getConexion();
                PreparedStatement ps = conexion.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapear(rs));
            }

        } catch (SQLException e) {
            throw new Exception("Error al listar libros: " + e.getMessage(), e);
        }

        return lista;
    }
    @Override
    public Libros buscarPorISBN(String isbn) throws Exception {

        String sql = "SELECT isbn, titulo, fecha_publicacion, precio, "
                + "id_categoria, nit_editorial, stock "
                + "FROM libros WHERE isbn = ?";

        try (Connection conexion = Conexion.getInstancia().getConexion();
                PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, isbn);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return mapear(rs);
                }
            }

        } catch (SQLException e) {
            throw new Exception("Error al buscar por ISBN: "
                    + e.getMessage(), e);
        }

        return null;
    }
    @Override
    public List<Libros> buscarPorTitulo(String titulo) throws Exception {

        List<Libros> lista = new ArrayList<>();

        String sql = "SELECT isbn, titulo, fecha_publicacion, precio, "
                + "id_categoria, nit_editorial, stock "
                + "FROM libros "
                + "WHERE titulo LIKE ? "
                + "ORDER BY titulo";

        try (Connection conexion = Conexion.getInstancia().getConexion();
                PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, "%" + titulo + "%");

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }

        } catch (SQLException e) {
            throw new Exception("Error al buscar por título: "
                    + e.getMessage(), e);
        }

        return lista;
    }

    @Override
    public void actualizar(Libros libro) throws Exception {

        String sql = "UPDATE libros SET "
                + "titulo = ?, "
                + "fecha_publicacion = ?, "
                + "precio = ?, "
                + "id_categoria = ?, "
                + "nit_editorial = ?, "
                + "stock = ? "
                + "WHERE isbn = ?";

        try (Connection conexion = Conexion.getInstancia().getConexion();
                PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, libro.getTitulo());

            if (libro.getFechaPublicacion() == null) {
                ps.setNull(2, Types.DATE);
            } else {
                ps.setDate(2,
                        Date.valueOf(libro.getFechaPublicacion()));
            }

            ps.setBigDecimal(3, libro.getPrecio());
            ps.setInt(4, libro.getIdCategoria());
            ps.setString(5, libro.getNitEditorial());
            ps.setInt(6, libro.getStock());
            ps.setString(7, libro.getIsbn());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new Exception("Error al actualizar libro: "
                    + e.getMessage(), e);
        }
    }

    @Override
    public void eliminar(String isbn) throws Exception {

        String sql = "DELETE FROM libros WHERE isbn = ?";

        try (Connection conexion = Conexion.getInstancia().getConexion();
                PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, isbn);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new Exception("Error al eliminar libro: "
                    + e.getMessage(), e);
        }
    }

    @Override
    public boolean validarStock(String isbn, int cantidad)
            throws Exception {

        if (cantidad <= 0) {
            return false;
        }

        String sql = "SELECT stock FROM libros WHERE isbn = ?";

        try (Connection conexion = Conexion.getInstancia().getConexion();
                PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, isbn);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    int stock = rs.getInt("stock");
                    return stock >= cantidad;
                }
            }

        } catch (SQLException e) {
            throw new Exception("Error al validar stock: "
                    + e.getMessage(), e);
        }

        return false;
    }

    @Override
    public boolean actualizarStock(String isbn, int cantidad)
            throws Exception {

        if (cantidad <= 0) {
            return false;
        }

        String sql = "UPDATE libros "
                + "SET stock = stock - ? "
                + "WHERE isbn = ? AND stock >= ?";

        try (Connection conexion = Conexion.getInstancia().getConexion();
                PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, cantidad);
            ps.setString(2, isbn);
            ps.setInt(3, cantidad);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new Exception("Error al actualizar stock: "
                    + e.getMessage(), e);
        }
    }
}