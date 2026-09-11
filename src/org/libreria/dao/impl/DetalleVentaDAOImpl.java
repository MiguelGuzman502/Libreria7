package org.libreria.dao.impl;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.libreria.dao.DetalleVentaDAO;
import org.libreria.model.DetalleVenta;
import org.libreria.util.Conexion;

public class DetalleVentaDAOImpl implements DetalleVentaDAO {

    private boolean existeColumna(Connection conexion, String tabla, String columna) throws SQLException {
        try (ResultSet rs = conexion.getMetaData().getColumns(null, null, tabla, columna)) {
            return rs.next();
        }
    }

    private String obtenerColumnaCantidad(Connection conexion) throws SQLException {
        String[] columnas = {
            "cantidad", "cantidad_libros", "cantidad_producto", "cantidad_vendida",
            "cantidad_compra", "unidades", "unidades_vendidas", "cant", "qty"
        };

        for (String columna : columnas) {
            if (existeColumna(conexion, "detalle_compra", columna)) {
                return columna;
            }
        }
        return null;
    }

    private boolean tieneSubtotal(Connection conexion) throws SQLException {
        return existeColumna(conexion, "detalle_compra", "subtotal");
    }

    @Override
    public void insertarMasivo(int idVenta, List<DetalleVenta> detalles) throws Exception {
        if (detalles == null || detalles.isEmpty()) {
            return;
        }

        try (Connection conexion = Conexion.getInstancia().conectar()) {
            String columnaCantidad = obtenerColumnaCantidad(conexion);
            boolean subtotalExiste = tieneSubtotal(conexion);

            StringBuilder sql = new StringBuilder("INSERT INTO detalle_compra (no_compra, isbn");
            if (columnaCantidad != null) {
                sql.append(", ").append(columnaCantidad);
            }
            if (subtotalExiste) {
                sql.append(", subtotal");
            }
            sql.append(") VALUES (?, ?");
            if (columnaCantidad != null) {
                sql.append(", ?");
            }
            if (subtotalExiste) {
                sql.append(", ?");
            }
            sql.append(")");

            try (PreparedStatement ps = conexion.prepareStatement(sql.toString())) {
                for (DetalleVenta detalle : detalles) {
                    int indice = 1;
                    ps.setInt(indice++, idVenta);
                    ps.setString(indice++, detalle.getIsbn());

                    if (columnaCantidad != null) {
                        ps.setInt(indice++, detalle.getCantidad());
                    }
                    if (subtotalExiste) {
                        ps.setBigDecimal(indice, calcularSubtotal(detalle));
                    }
                    ps.addBatch();
                }
                ps.executeBatch();
            }
        } catch (SQLException e) {
            throw new Exception("Error al insertar detalle de venta: " + e.getMessage(), e);
        }
    }

    @Override
    public List<DetalleVenta> listarPorVenta(int idVenta) throws Exception {
        List<DetalleVenta> lista = new ArrayList<>();

        try (Connection conexion = Conexion.getInstancia().conectar()) {
            String columnaCantidad = obtenerColumnaCantidad(conexion);
            boolean subtotalExiste = tieneSubtotal(conexion);

            StringBuilder sql = new StringBuilder();
            sql.append("SELECT d.no_compra, d.isbn");

            if (columnaCantidad != null) {
                sql.append(", d.").append(columnaCantidad).append(" AS cantidad");
            }

            if (subtotalExiste) {
                sql.append(", d.subtotal");
            }

            sql.append(", l.titulo, l.precio ")
               .append("FROM detalle_compra d LEFT JOIN libros l ON l.isbn = d.isbn ")
               .append("WHERE d.no_compra = ? ORDER BY d.isbn");

            try (PreparedStatement ps = conexion.prepareStatement(sql.toString())) {
                ps.setInt(1, idVenta);

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        DetalleVenta detalle = new DetalleVenta();
                        detalle.setIdVenta(rs.getInt("no_compra"));
                        detalle.setIsbn(rs.getString("isbn"));
                        detalle.setProducto(rs.getString("titulo"));

                        BigDecimal precio = rs.getBigDecimal("precio");
                        if (precio != null) {
                            detalle.setPrecio(precio.doubleValue());
                        }

                        int cantidad;
                        if (columnaCantidad != null) {
                            cantidad = rs.getInt("cantidad");
                        } else {
                            cantidad = 1;
                        }
                        detalle.setCantidad(cantidad);

                        BigDecimal subtotal = subtotalExiste ? rs.getBigDecimal("subtotal") : null;
                        if (subtotal == null) {
                            subtotal = calcularSubtotal(detalle);
                        }
                        detalle.setSubtotal(subtotal);
                        lista.add(detalle);
                    }
                }
            }
        } catch (SQLException e) {
            throw new Exception("Error al consultar el detalle de la venta: " + e.getMessage(), e);
        }

        return lista;
    }

    private BigDecimal calcularSubtotal(DetalleVenta detalle) {
        if (detalle == null || detalle.getCantidad() <= 0) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(detalle.getPrecio())
                .multiply(BigDecimal.valueOf(detalle.getCantidad()));
    }
}
