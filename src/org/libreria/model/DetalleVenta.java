package org.libreria.model;

import java.math.BigDecimal;

public class DetalleVenta {

    private int idVenta;
    private String isbn;
    private String producto;
    private int cantidad;
    private double precio;
    private BigDecimal subtotal;

    public DetalleVenta() {
    }

    // Constructor utilizado por la parte de comprobantes
    public DetalleVenta(String producto, int cantidad, double precio) {
        this.producto = producto;
        this.cantidad = cantidad;
        this.precio = precio;
        this.subtotal = BigDecimal.valueOf(cantidad * precio);
    }

    // Constructor utilizado por DAO
    public DetalleVenta(
            int idVenta,
            String isbn,
            int cantidad,
            BigDecimal subtotal) {

        this.idVenta = idVenta;
        this.isbn = isbn;
        this.cantidad = cantidad;
        this.subtotal = subtotal;
    }

    public int getIdVenta() {
        return idVenta;
    }

    public void setIdVenta(int idVenta) {
        this.idVenta = idVenta;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getProducto() {
        return producto;
    }

    public void setProducto(String producto) {
        this.producto = producto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
        recalcularSubtotal();
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
        recalcularSubtotal();
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    private void recalcularSubtotal() {

        if (precio > 0 && cantidad > 0) {
            subtotal = BigDecimal.valueOf(
                    cantidad * precio
            );
        }
    }
}