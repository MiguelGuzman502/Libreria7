package org.libreria.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.libreria.dao.DetalleVentaDAO;
import org.libreria.model.DetalleVenta;
import org.libreria.util.Conexion;

public class DetalleVentaDAOImpl implements DetalleVentaDAO {

    @Override
    public void insertarMasivo(int idVenta,
            List<DetalleVenta> detalles) throws Exception {

        String sql = "INSERT INTO detalle_compra "
                + "(no_compra, isbn, cantidad, subtotal) "
                + "VALUES (?, ?, ?, ?)";

        try (Connection conexion =
                Conexion.getInstancia().getConexion();
                PreparedStatement ps =
                conexion.prepareStatement(sql)) {

            conexion.setAutoCommit(false);

            try {

                for (DetalleVenta detalle : detalles) {

                    ps.setInt(1, idVenta);
                    ps.setString(2, detalle.getIsbn());
                    ps.setInt(3, detalle.getCantidad());
                    ps.setBigDecimal(4, detalle.getSubtotal());

                    ps.addBatch();
                }

                ps.executeBatch();

                conexion.commit();

            } catch (SQLException e) {

                conexion.rollback();

                throw e;

            } finally {

                conexion.setAutoCommit(true);
            }

        } catch (SQLException e) {

            throw new Exception(
                    "Error al insertar detalle de venta: "
                    + e.getMessage(), e);
        }
    }
}