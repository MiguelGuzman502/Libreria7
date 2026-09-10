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

    @Override
    public void insertarMasivo(int idVenta, List<DetalleVenta> detalles) throws Exception {
        String sql = "INSERT INTO detalle_compra (no_compra, isbn, cantidad, subtotal) VALUES (?, ?, ?, ?)";
        try (Connection conexion = Conexion.getInstancia().conectar(); PreparedStatement ps = conexion.prepareStatement(sql)) {
            for (DetalleVenta detalle : detalles) {
                ps.setInt(1, idVenta);
                ps.setString(2, detalle.getIsbn());
                ps.setInt(3, detalle.getCantidad());
                ps.setBigDecimal(4, detalle.getSubtotal());
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (SQLException e) {
            throw new Exception("Error al insertar detalle de venta: " + e.getMessage(), e);
        }
    }

    @Override
    public List<DetalleVenta> listarPorVenta(int idVenta) throws Exception {
        List<DetalleVenta> lista = new ArrayList<>();
        String sql = "SELECT d.no_compra, d.isbn, d.cantidad, d.subtotal, l.titulo, l.precio FROM detalle_compra d LEFT JOIN libros l ON l.isbn = d.isbn WHERE d.no_compra = ? ORDER BY d.isbn";
        try (Connection conexion = Conexion.getInstancia().conectar(); PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, idVenta);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DetalleVenta detalle = new DetalleVenta();
                    detalle.setIdVenta(rs.getInt("no_compra"));
                    detalle.setIsbn(rs.getString("isbn"));
                    detalle.setCantidad(rs.getInt("cantidad"));
                    detalle.setSubtotal(rs.getBigDecimal("subtotal"));
                    detalle.setProducto(rs.getString("titulo"));
                    BigDecimal precio = rs.getBigDecimal("precio");
                    if (precio != null) detalle.setPrecio(precio.doubleValue());
                    else if (detalle.getCantidad() > 0 && detalle.getSubtotal() != null) detalle.setPrecio(detalle.getSubtotal().doubleValue() / detalle.getCantidad());
                    lista.add(detalle);
                }
            }
        } catch (SQLException e) {
            throw new Exception("Error al consultar el detalle de la venta: " + e.getMessage(), e);
        }
        return lista;
    }
}
