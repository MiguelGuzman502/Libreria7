package org.libreria.model;

import java.math.BigDecimal;

public class DetalleVenta {

    private int idVenta;
    private String isbn;
    private int cantidad;
    private BigDecimal subtotal;

    public DetalleVenta() {
    }

    public DetalleVenta(int idVenta, String isbn,
            int cantidad, BigDecimal subtotal) {

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

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }
}
