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

    private String obtenerColumnaStock(Connection conexion) throws SQLException {
        String[] posibles = {
            "stock",
            "existencia",
            "existencias",
            "cantidad",
            "cantidad_stock",
            "cantidad_disponible",
            "disponibles",
            "unidades"
        };

        for (String columna : posibles) {
            try (ResultSet rs = conexion.getMetaData().getColumns(null, null, "libros", columna)) {
                if (rs.next()) {
                    return columna;
                }
            }
        }

        try (PreparedStatement ps = conexion.prepareStatement(
                "ALTER TABLE libros ADD COLUMN stock INT NOT NULL DEFAULT 0")) {
            ps.executeUpdate();
        }

        return "stock";
    }

    private boolean tieneAutor(Connection conexion) throws SQLException {
        try (ResultSet rs = conexion.getMetaData().getColumns(null, null, "libros", "autor")) {
            return rs.next();
        }
    }

    private String columnas(Connection conexion) throws SQLException {
        StringBuilder sql = new StringBuilder();
        sql.append("isbn, titulo, fecha_publicacion, precio, id_categoria, nit_editorial");

        String stock = obtenerColumnaStock(conexion);
        sql.append(", ").append(stock).append(" AS stock");

        if (tieneAutor(conexion)) {
            sql.append(", autor");
        }

        return sql.toString();
    }

    private Libros mapear(ResultSet rs, boolean autor) throws SQLException {
        Libros libro = new Libros();

        libro.setIsbn(rs.getString("isbn"));
        libro.setTitulo(rs.getString("titulo"));

        if (autor) {
            libro.setAutor(rs.getString("autor"));
        } else {
            libro.setAutor("");
        }

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
        try (Connection conexion = Conexion.getInstancia().conectar()) {

            String stock = obtenerColumnaStock(conexion);

            String sql = "INSERT INTO libros "
                    + "(isbn, titulo, fecha_publicacion, precio, id_categoria, nit_editorial, "
                    + stock + ") VALUES (?, ?, ?, ?, ?, ?, ?)";

            try (PreparedStatement ps = conexion.prepareStatement(sql)) {

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
            }

        } catch (SQLException e) {
            throw new Exception("Error al insertar libro: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Libros> listar() throws Exception {
        List<Libros> lista = new ArrayList<>();

        try (Connection conexion = Conexion.getInstancia().conectar()) {

            boolean autor = tieneAutor(conexion);

            String sql = "SELECT "
                    + columnas(conexion)
                    + " FROM libros ORDER BY titulo";

            try (PreparedStatement ps = conexion.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    lista.add(mapear(rs, autor));
                }
            }

        } catch (SQLException e) {
            throw new Exception("Error al listar libros: " + e.getMessage(), e);
        }

        return lista;
    }

    @Override
    public Libros buscarPorISBN(String isbn) throws Exception {
        try (Connection conexion = Conexion.getInstancia().conectar()) {

            boolean autor = tieneAutor(conexion);

            String sql = "SELECT "
                    + columnas(conexion)
                    + " FROM libros WHERE isbn = ?";

            try (PreparedStatement ps = conexion.prepareStatement(sql)) {

                ps.setString(1, isbn);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return mapear(rs, autor);
                    }
                }
            }

        } catch (SQLException e) {
            throw new Exception("Error al buscar por ISBN: " + e.getMessage(), e);
        }

        return null;
    }

    @Override
    public List<Libros> buscarPorTitulo(String titulo) throws Exception {
        return buscarPorCampo("titulo", titulo);
    }

    @Override
    public List<Libros> buscarPorAutor(String autorTexto) throws Exception {
        List<Libros> lista = new ArrayList<>();

        try (Connection conexion = Conexion.getInstancia().conectar()) {

            if (!tieneAutor(conexion)) {
                return lista;
            }

            String sql = "SELECT "
                    + columnas(conexion)
                    + " FROM libros WHERE autor LIKE ? ORDER BY titulo";

            try (PreparedStatement ps = conexion.prepareStatement(sql)) {

                ps.setString(1, "%" + autorTexto + "%");

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        lista.add(mapear(rs, true));
                    }
                }
            }

        } catch (SQLException e) {
            throw new Exception("Error al buscar por autor: " + e.getMessage(), e);
        }

        return lista;
    }

    private List<Libros> buscarPorCampo(String campo, String valor) throws Exception {
        List<Libros> lista = new ArrayList<>();

        try (Connection conexion = Conexion.getInstancia().conectar()) {

            boolean autor = tieneAutor(conexion);

            String sql = "SELECT "
                    + columnas(conexion)
                    + " FROM libros WHERE "
                    + campo
                    + " LIKE ? ORDER BY titulo";

            try (PreparedStatement ps = conexion.prepareStatement(sql)) {

                ps.setString(1, "%" + valor + "%");

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        lista.add(mapear(rs, autor));
                    }
                }
            }

        } catch (SQLException e) {
            throw new Exception("Error al buscar por " + campo + ": " + e.getMessage(), e);
        }

        return lista;
    }

    @Override
    public void actualizar(Libros libro) throws Exception {
        try (Connection conexion = Conexion.getInstancia().conectar()) {

            String stock = obtenerColumnaStock(conexion);

            String sql = "UPDATE libros SET titulo = ?, fecha_publicacion = ?, precio = ?, "
                    + "id_categoria = ?, nit_editorial = ?, "
                    + stock + " = ? WHERE isbn = ?";

            try (PreparedStatement ps = conexion.prepareStatement(sql)) {

                ps.setString(1, libro.getTitulo());

                if (libro.getFechaPublicacion() == null) {
                    ps.setNull(2, Types.DATE);
                } else {
                    ps.setDate(2, Date.valueOf(libro.getFechaPublicacion()));
                }

                ps.setBigDecimal(3, libro.getPrecio());
                ps.setInt(4, libro.getIdCategoria());
                ps.setString(5, libro.getNitEditorial());
                ps.setInt(6, libro.getStock());
                ps.setString(7, libro.getIsbn());

                ps.executeUpdate();
            }

        } catch (SQLException e) {
            throw new Exception("Error al actualizar libro: " + e.getMessage(), e);
        }
    }

    @Override
    public void eliminar(String isbn) throws Exception {
        try (Connection conexion = Conexion.getInstancia().conectar();
             PreparedStatement ps = conexion.prepareStatement(
                     "DELETE FROM libros WHERE isbn = ?")) {

            ps.setString(1, isbn);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new Exception("Error al eliminar libro: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean validarStock(String isbn, int cantidad) throws Exception {
        if (cantidad <= 0) {
            return false;
        }

        try (Connection conexion = Conexion.getInstancia().conectar()) {

            String stock = obtenerColumnaStock(conexion);

            String sql = "SELECT " + stock + " AS stock FROM libros WHERE isbn = ?";

            try (PreparedStatement ps = conexion.prepareStatement(sql)) {

                ps.setString(1, isbn);

                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next() && rs.getInt("stock") >= cantidad;
                }
            }

        } catch (SQLException e) {
            throw new Exception("Error al validar stock: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean agregarStock(String isbn, int cantidad) throws Exception {
        if (cantidad <= 0) {
            return false;
        }

        try (Connection conexion = Conexion.getInstancia().conectar()) {

            String stock = obtenerColumnaStock(conexion);

            String sql = "UPDATE libros SET "
                    + stock
                    + " = "
                    + stock
                    + " + ? WHERE isbn = ?";

            try (PreparedStatement ps = conexion.prepareStatement(sql)) {

                ps.setInt(1, cantidad);
                ps.setString(2, isbn);

                return ps.executeUpdate() > 0;
            }

        } catch (SQLException e) {
            throw new Exception("Error al agregar stock: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean actualizarStock(String isbn, int cantidad) throws Exception {
        if (cantidad <= 0) {
            return false;
        }

        try (Connection conexion = Conexion.getInstancia().conectar()) {

            String stock = obtenerColumnaStock(conexion);

            String sql = "UPDATE libros SET "
                    + stock
                    + " = "
                    + stock
                    + " - ? WHERE isbn = ? AND "
                    + stock
                    + " >= ?";

            try (PreparedStatement ps = conexion.prepareStatement(sql)) {

                ps.setInt(1, cantidad);
                ps.setString(2, isbn);
                ps.setInt(3, cantidad);

                return ps.executeUpdate() > 0;
            }

        } catch (SQLException e) {
            throw new Exception("Error al actualizar stock: " + e.getMessage(), e);
        }
    }
}