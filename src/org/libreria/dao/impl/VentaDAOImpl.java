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

    private Venta mapear(ResultSet rs) throws SQLException {
        Venta venta = new Venta();
        venta.setId(rs.getInt("no_compra"));
        Timestamp fecha = rs.getTimestamp("fecha_compra");
        if (fecha != null) venta.setFecha(fecha.toLocalDateTime());
        venta.setTotal(rs.getBigDecimal("total_compra"));
        venta.setCuiCliente(rs.getLong("cui_cliente"));
        return venta;
    }

    @Override
    public int insertar(Venta venta) throws Exception {
        String sql = "INSERT INTO compras (fecha_compra, total_compra, cui_cliente) VALUES (?, ?, ?)";
        try (Connection conexion = Conexion.getInstancia().conectar();
             PreparedStatement ps = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setTimestamp(1, venta.getFecha() == null ? new Timestamp(System.currentTimeMillis()) : Timestamp.valueOf(venta.getFecha()));
            ps.setBigDecimal(2, venta.getTotal());
            if (venta.getCuiCliente() > 0) ps.setLong(3, venta.getCuiCliente());
            else ps.setNull(3, java.sql.Types.BIGINT);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    venta.setId(rs.getInt(1));
                    return venta.getId();
                }
            }
        } catch (SQLException e) {
            throw new Exception("Error al insertar venta: " + e.getMessage(), e);
        }
        throw new Exception("No se pudo obtener el ID de la venta.");
    }

    @Override
    public Venta buscarPorId(int id) throws Exception {
        String sql = "SELECT no_compra, fecha_compra, total_compra, cui_cliente FROM compras WHERE no_compra = ?";
        try (Connection conexion = Conexion.getInstancia().conectar(); PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        } catch (SQLException e) {
            throw new Exception("Error al buscar venta: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public List<Venta> ventasDelDia() throws Exception {
        List<Venta> lista = new ArrayList<>();
        String sql = "SELECT no_compra, fecha_compra, total_compra, cui_cliente FROM compras WHERE fecha_compra >= CURDATE() AND fecha_compra < CURDATE() + INTERVAL 1 DAY ORDER BY fecha_compra DESC";
        try (Connection conexion = Conexion.getInstancia().conectar(); PreparedStatement ps = conexion.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            throw new Exception("Error al consultar ventas del día: " + e.getMessage(), e);
        }
        return lista;
    }
}
