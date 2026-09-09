package org.libreria.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Venta {

    // Datos utilizados por DAO / base de datos
    private int id;
    private LocalDateTime fecha;
    private BigDecimal total;
    private long cuiCliente;
    private String usuario;

    // Datos utilizados por comprobantes
    private int idVenta;
    private double montoTotal;
    private List<DetalleVenta> detalles;

    public Venta() {

        fecha = LocalDateTime.now();

        total = BigDecimal.ZERO;

        montoTotal = 0.0;

        detalles = new ArrayList<>();
    }

    // Constructor utilizado por DAO
    public Venta(
            int id,
            LocalDateTime fecha,
            BigDecimal total,
            long cuiCliente,
            String usuario) {

        this.id = id;
        this.idVenta = id;

        this.fecha = fecha;

        this.total = total;

        if (total != null) {
            this.montoTotal = total.doubleValue();
        }

        this.cuiCliente = cuiCliente;

        this.usuario = usuario;

        this.detalles = new ArrayList<>();
    }

    // Constructor utilizado por comprobantes
    public Venta(
            int idVenta,
            LocalDateTime fecha,
            double montoTotal) {

        this.idVenta = idVenta;

        this.id = idVenta;

        this.fecha = fecha;

        this.montoTotal = montoTotal;

        this.total = BigDecimal.valueOf(montoTotal);

        this.detalles = new ArrayList<>();
    }

    // =====================================================
    // DETALLES
    // =====================================================

    public void agregarDetalle(DetalleVenta detalle) {

        if (detalle == null) {
            return;
        }

        detalles.add(detalle);

        calcularTotal();
    }

    private void calcularTotal() {

        montoTotal = 0.0;

        for (DetalleVenta detalle : detalles) {

            if (detalle.getSubtotal() != null) {

                montoTotal +=
                        detalle.getSubtotal().doubleValue();
            }
        }

        total = BigDecimal.valueOf(montoTotal);
    }

    public List<DetalleVenta> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleVenta> detalles) {

        if (detalles == null) {
            this.detalles = new ArrayList<>();
        } else {
            this.detalles = detalles;
        }

        calcularTotal();
    }

    // =====================================================
    // ID
    // =====================================================

    public int getId() {
        return id;
    }

    public void setId(int id) {

        this.id = id;

        this.idVenta = id;
    }

    public int getIdVenta() {
        return idVenta;
    }

    public void setIdVenta(int idVenta) {

        this.idVenta = idVenta;

        this.id = idVenta;
    }

    // =====================================================
    // FECHA
    // =====================================================

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    // =====================================================
    // TOTAL
    // =====================================================

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {

        this.total = total;

        if (total != null) {
            this.montoTotal = total.doubleValue();
        }
    }

    public double getMontoTotal() {
        return montoTotal;
    }

    public void setMontoTotal(double montoTotal) {

        this.montoTotal = montoTotal;

        this.total = BigDecimal.valueOf(montoTotal);
    }

    // =====================================================
    // CLIENTE
    // =====================================================

    public long getCuiCliente() {
        return cuiCliente;
    }

    public void setCuiCliente(long cuiCliente) {
        this.cuiCliente = cuiCliente;
    }

    // =====================================================
    // USUARIO
    // =====================================================

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }
}