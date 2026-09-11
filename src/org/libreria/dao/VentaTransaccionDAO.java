package org.libreria.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import org.libreria.model.DetalleVenta;
import org.libreria.model.Venta;
import org.libreria.util.Conexion;

public class VentaTransaccionDAO {

    private String obtenerColumnaStock(Connection conexion) throws SQLException {
        String[] posibles = {"stock", "existencia", "existencias", "cantidad", "cantidad_stock", "cantidad_disponible", "disponibles", "unidades"};
        for (String columna : posibles) {
            try (ResultSet rs = conexion.getMetaData().getColumns(null, null, "libros", columna)) {
                if (rs.next()) {
                    return columna;
                }
            }
        }
        try (PreparedStatement ps = conexion.prepareStatement("ALTER TABLE libros ADD COLUMN stock INT NOT NULL DEFAULT 0")) {
            ps.executeUpdate();
        }
        return "stock";
    }

    public Venta registrarVentaTransaccional(Venta venta, List<DetalleVenta> detalles) throws Exception {
        if (venta == null || detalles == null || detalles.isEmpty()) {
            throw new Exception("La venta debe tener al menos un producto.");
        }

        BigDecimal total = BigDecimal.ZERO;
        for (DetalleVenta detalle : detalles) {
            if (detalle == null || detalle.getIsbn() == null || detalle.getIsbn().trim().isEmpty() || detalle.getCantidad() <= 0 || detalle.getSubtotal() == null || detalle.getSubtotal().compareTo(BigDecimal.ZERO) < 0) {
                throw new Exception("La venta contiene un detalle inválido.");
            }
            total = total.add(detalle.getSubtotal());
        }

        if (total.compareTo(BigDecimal.ZERO) <= 0) {
            throw new Exception("El total de la venta debe ser mayor que cero.");
        }

        String sqlVenta = "INSERT INTO compras (fecha_compra, total_compra, cui_cliente) VALUES (?, ?, ?)";
        String sqlDetalle = "INSERT INTO detalle_compra (no_compra, isbn, cantidad, subtotal) VALUES (?, ?, ?, ?)";

        try (Connection conn = Conexion.getInstancia().conectar()) {
            String columnaStock = obtenerColumnaStock(conn);
            conn.setAutoCommit(false);
            try {
                int idVenta;
                try (PreparedStatement psVenta = conn.prepareStatement(sqlVenta, Statement.RETURN_GENERATED_KEYS)) {
                    LocalDateTimeHelper.setFecha(psVenta, venta.getFecha());
                    psVenta.setBigDecimal(2, total);
                    if (venta.getCuiCliente() > 0) {
                        psVenta.setLong(3, venta.getCuiCliente());
                    } else {
                        psVenta.setNull(3, java.sql.Types.BIGINT);
                    }
                    psVenta.executeUpdate();
                    try (ResultSet keys = psVenta.getGeneratedKeys()) {
                        if (!keys.next()) {
                            throw new SQLException("No se obtuvo el ID de la venta.");
                        }
                        idVenta = keys.getInt(1);
                    }
                }

                String sqlStock = "UPDATE libros SET " + columnaStock + " = " + columnaStock + " - ? WHERE isbn = ? AND " + columnaStock + " >= ?";
                try (PreparedStatement psStock = conn.prepareStatement(sqlStock); PreparedStatement psDetalle = conn.prepareStatement(sqlDetalle)) {
                    for (DetalleVenta detalle : detalles) {
                        psStock.setInt(1, detalle.getCantidad());
                        psStock.setString(2, detalle.getIsbn());
                        psStock.setInt(3, detalle.getCantidad());
                        if (psStock.executeUpdate() != 1) {
                            throw new SQLException("Stock insuficiente o libro inexistente: " + detalle.getIsbn());
                        }
                        psDetalle.setInt(1, idVenta);
                        psDetalle.setString(2, detalle.getIsbn());
                        psDetalle.setInt(3, detalle.getCantidad());
                        psDetalle.setBigDecimal(4, detalle.getSubtotal());
                        psDetalle.addBatch();
                    }
                    psDetalle.executeBatch();
                }

                conn.commit();
                venta.setId(idVenta);
                venta.setTotal(total);
                if (venta.getFecha() == null) {
                    venta.setFecha(java.time.LocalDateTime.now());
                }
                return venta;
            } catch (Exception e) {
                try {
                    conn.rollback();
                } catch (SQLException ignored) {
                }
                throw new Exception("No se pudo registrar la venta: " + e.getMessage(), e);
            } finally {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException ignored) {
                }
            }
        } catch (SQLException e) {
            throw new Exception("Error de conexión o transacción: " + e.getMessage(), e);
        }
    }

    public boolean registrarVentaTransaccional(int idUsuario, double total, List<Object[]> detalles) {
        try {
            Venta venta = new Venta();
            venta.setTotal(BigDecimal.valueOf(total));
            venta.setCuiCliente(0);
            List<DetalleVenta> lista = new ArrayList<>();
            for (Object[] item : detalles) {
                DetalleVenta detalle = new DetalleVenta();
                detalle.setIsbn(String.valueOf(item[0]));
                detalle.setCantidad(((Number) item[1]).intValue());
                detalle.setSubtotal(BigDecimal.valueOf(((Number) item[2]).doubleValue()));
                lista.add(detalle);
            }
            registrarVentaTransaccional(venta, lista);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static class LocalDateTimeHelper {
        private static void setFecha(PreparedStatement ps, java.time.LocalDateTime fecha) throws SQLException {
            ps.setTimestamp(1, fecha == null ? new Timestamp(System.currentTimeMillis()) : Timestamp.valueOf(fecha));
        }
    }
}
