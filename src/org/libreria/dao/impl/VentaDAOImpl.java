package org.libreria.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.libreria.dao.VentaDAO;
import org.libreria.model.Venta;
import org.libreria.util.Conexion;

public class VentaDAOImpl implements VentaDAO {

    @Override
    public int insertar(Venta venta) throws Exception {

        String sql = "INSERT INTO compras "
                + "(fecha_compra, total_compra, cui_cliente) "
                + "VALUES (?, ?, ?)";

        try (Connection conexion = Conexion.getInstancia().getConexion();
                PreparedStatement ps = conexion.prepareStatement(
                        sql, Statement.RETURN_GENERATED_KEYS)) {

            if (venta.getFecha() == null) {
                ps.setTimestamp(1,
                        new Timestamp(System.currentTimeMillis()));
            } else {
                ps.setTimestamp(1,
                        Timestamp.valueOf(venta.getFecha()));
            }

            ps.setBigDecimal(2, venta.getTotal());
            ps.setLong(3, venta.getCuiCliente());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {

                if (rs.next()) {

                    int id = rs.getInt(1);

                    venta.setId(id);

                    return id;
                }
            }

        } catch (SQLException e) {
            throw new Exception("Error al insertar venta: "
                    + e.getMessage(), e);
        }

        throw new Exception("No se pudo obtener el ID de la venta.");
    }

    @Override
    public List<Venta> ventasDelDia() throws Exception {

        List<Venta> lista = new ArrayList<>();

        String sql = "SELECT no_compra, fecha_compra, "
                + "total_compra, cui_cliente "
                + "FROM compras "
                + "WHERE fecha_compra >= CURDATE() "
                + "AND fecha_compra < CURDATE() + INTERVAL 1 DAY "
                + "ORDER BY fecha_compra";

        try (Connection conexion = Conexion.getInstancia().getConexion();
                PreparedStatement ps = conexion.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                Venta venta = new Venta();

                venta.setId(rs.getInt("no_compra"));

                Timestamp fecha = rs.getTimestamp("fecha_compra");

                if (fecha != null) {
                    venta.setFecha(fecha.toLocalDateTime());
                }

                venta.setTotal(
                        rs.getBigDecimal("total_compra"));

                venta.setCuiCliente(
                        rs.getLong("cui_cliente"));

                lista.add(venta);
            }

        } catch (SQLException e) {
            throw new Exception(
                    "Error al consultar ventas del día: "
                    + e.getMessage(), e);
        }

        return lista;
    }
}