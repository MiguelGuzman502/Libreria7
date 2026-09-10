package org.libreria.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;
import org.libreria.model.DetalleVenta;
import org.libreria.model.Venta;
import org.libreria.util.Conexion;

public class VentaTransaccionDAO {

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

            try (ResultSet rs = conexion.getMetaData()
                    .getColumns(null, null, "libros", columna)) {

                if (rs.next()) {
                    return columna;
                }
            }
        }

        return null;
    }

    public Venta registrarVentaTransaccional(
            Venta venta,
            List<DetalleVenta> detalles) throws Exception {

        if (venta == null || detalles == null || detalles.isEmpty()) {
            throw new Exception("La venta debe tener al menos un detalle.");
        }

        String sqlVenta =
                "INSERT INTO compras "
                + "(fecha_compra, total_compra, cui_cliente) "
                + "VALUES (?, ?, ?)";

        String sqlDetalle =
                "INSERT INTO detalle_compra "
                + "(no_compra, isbn, cantidad, subtotal) "
                + "VALUES (?, ?, ?, ?)";

        try (Connection conn = Conexion.getInstancia().conectar()) {

            conn.setAutoCommit(false);

            try {

                int idVenta;

                try (PreparedStatement psVenta =
                        conn.prepareStatement(
                                sqlVenta,
                                Statement.RETURN_GENERATED_KEYS)) {

                    Timestamp fecha =
                            venta.getFecha() == null
                            ? new Timestamp(System.currentTimeMillis())
                            : Timestamp.valueOf(venta.getFecha());

                    psVenta.setTimestamp(1, fecha);
                    psVenta.setBigDecimal(2, venta.getTotal());

                    if (venta.getCuiCliente() > 0) {
                        psVenta.setLong(3, venta.getCuiCliente());
                    } else {
                        psVenta.setNull(3, java.sql.Types.BIGINT);
                    }

                    psVenta.executeUpdate();

                    try (ResultSet keys = psVenta.getGeneratedKeys()) {

                        if (!keys.next()) {
                            throw new SQLException(
                                    "No se obtuvo el ID de la venta.");
                        }

                        idVenta = keys.getInt(1);
                    }
                }

                String columnaStock = obtenerColumnaStock(conn);

                String sqlStock = null;

                if (columnaStock != null) {
                    sqlStock =
                            "UPDATE libros SET "
                            + columnaStock
                            + " = "
                            + columnaStock
                            + " - ? WHERE isbn = ? AND "
                            + columnaStock
                            + " >= ?";
                }

                try (PreparedStatement psDetalle =
                        conn.prepareStatement(sqlDetalle);
                     PreparedStatement psStock =
                        sqlStock == null
                        ? null
                        : conn.prepareStatement(sqlStock)) {

                    for (DetalleVenta detalle : detalles) {

                        if (detalle.getIsbn() == null
                                || detalle.getIsbn().trim().isEmpty()
                                || detalle.getCantidad() <= 0) {

                            throw new SQLException(
                                    "Detalle de venta inválido.");
                        }

                        if (psStock != null) {

                            psStock.setInt(1, detalle.getCantidad());
                            psStock.setString(2, detalle.getIsbn());
                            psStock.setInt(3, detalle.getCantidad());

                            if (psStock.executeUpdate() != 1) {

                                throw new SQLException(
                                        "Stock insuficiente para ISBN "
                                        + detalle.getIsbn());
                            }
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

                if (venta.getFecha() == null) {
                    venta.setFecha(
                            new Timestamp(
                                    System.currentTimeMillis())
                                    .toLocalDateTime());
                }

                return venta;

            } catch (Exception e) {

                try {
                    conn.rollback();
                } catch (SQLException ignored) {
                }

                throw new Exception(
                        "No se pudo registrar la venta. "
                        + "Se ejecutó rollback: "
                        + e.getMessage(),
                        e);

            } finally {

                try {
                    conn.setAutoCommit(true);
                } catch (SQLException ignored) {
                }
            }

        } catch (SQLException e) {

            throw new Exception(
                    "Error de conexión o transacción: "
                    + e.getMessage(),
                    e);
        }
    }

    public boolean registrarVentaTransaccional(
            int idUsuario,
            double total,
            List<Object[]> detalles) {

        try {

            Venta venta = new Venta();

            venta.setTotal(BigDecimal.valueOf(total));
            venta.setCuiCliente(0);

            java.util.ArrayList<DetalleVenta> lista =
                    new java.util.ArrayList<>();

            for (Object[] item : detalles) {

                DetalleVenta detalle = new DetalleVenta();

                detalle.setIsbn(String.valueOf(item[0]));
                detalle.setCantidad(
                        ((Number) item[1]).intValue());

                detalle.setSubtotal(
                        BigDecimal.valueOf(
                                ((Number) item[2]).doubleValue()));

                lista.add(detalle);
            }

            registrarVentaTransaccional(venta, lista);

            return true;

        } catch (Exception e) {

            return false;
        }
    }
}