package org.libreria.dao;

import org.libreria.util.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class VentaTransaccionDAO {

    public boolean registrarVentaTransaccional(int idUsuario, double total, List<Object[]> detalles) {
        Connection conn = null;
        PreparedStatement stmtVenta = null;
        PreparedStatement stmtDetalle = null;
        ResultSet rsKey = null;

        String sqlVenta = "INSERT INTO venta (id_usuario, fecha, total) VALUES (?, NOW(), ?)";
        String sqlDetalle = "INSERT INTO detalle_venta (id_venta, id_libro, cantidad, subtotal) VALUES (?, ?, ?, ?)";

        try {
            conn = Conexion.getInstancia().conectar();
            conn.setAutoCommit(false); // Iniciar transacción

            // 1. Insertar venta principal
            stmtVenta = conn.prepareStatement(sqlVenta, PreparedStatement.RETURN_GENERATED_KEYS);
            stmtVenta.setInt(1, idUsuario);
            stmtVenta.setDouble(2, total);
            stmtVenta.executeUpdate();

            rsKey = stmtVenta.getGeneratedKeys();
            int idVentaGenerado = 0;
            if (rsKey.next()) {
                idVentaGenerado = rsKey.getInt(1);
            }

            // 2. Insertar detalles
            stmtDetalle = conn.prepareStatement(sqlDetalle);

            for (Object[] item : detalles) {
                int idLibroDetalle = (int) item[0];
                int cantidad = (int) item[1];
                double subtotal = (double) item[2];

                stmtDetalle.setInt(1, idVentaGenerado);
                stmtDetalle.setInt(2, idLibroDetalle);
                stmtDetalle.setInt(3, cantidad);
                stmtDetalle.setDouble(4, subtotal);
                stmtDetalle.addBatch();
            }

            stmtDetalle.executeBatch();

            conn.commit(); // Confirmar transacción
            return true;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    System.err.println("Falla en la transacción de venta. Ejecutando Rollback: " + e.getMessage());
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            return false;
        } finally {
            try {
                if (rsKey != null) rsKey.close();
                if (stmtVenta != null) stmtVenta.close();
                if (stmtDetalle != null) stmtDetalle.close();
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
}