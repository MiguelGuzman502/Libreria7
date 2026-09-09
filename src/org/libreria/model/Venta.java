package org.libreria.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Venta {

    private int idVenta;
    private LocalDateTime fecha;
    private double montoTotal;
    private List<DetalleVenta> detalles;

    public Venta() {
        detalles = new ArrayList<>();
        fecha = LocalDateTime.now();
    }

    public Venta(int idVenta, LocalDateTime fecha, double montoTotal) {
        this.idVenta = idVenta;
        this.fecha = fecha;
        this.montoTotal = montoTotal;
        this.detalles = new ArrayList<>();
    }

    public void agregarDetalle(DetalleVenta detalle) {
        detalles.add(detalle);
        calcularTotal();
    }

    private void calcularTotal() {
        montoTotal = 0;

        for (DetalleVenta detalle : detalles) {
            montoTotal += detalle.getSubtotal();
        }
    }

    public int getIdVenta() {
        return idVenta;
    }

    public void setIdVenta(int idVenta) {
        this.idVenta = idVenta;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public double getMontoTotal() {
        return montoTotal;
    }

    public List<DetalleVenta> getDetalles() {
        return detalles;
    }
}