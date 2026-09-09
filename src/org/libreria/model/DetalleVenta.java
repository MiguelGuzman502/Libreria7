package org.libreria.model;

<<<<<<< HEAD
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
=======
public class DetalleVenta {

    private String producto;
    private int cantidad;
    private double precio;
    private double subtotal;

    public DetalleVenta(String producto, int cantidad, double precio) {
        this.producto = producto;
        this.cantidad = cantidad;
        this.precio = precio;
        this.subtotal = cantidad * precio;
    }

    public String getProducto() {
        return producto;
>>>>>>> origin/feature/emilio-comprobantes-pruebas
    }

    public int getCantidad() {
        return cantidad;
    }

<<<<<<< HEAD
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
=======
    public double getPrecio() {
        return precio;
    }

    public double getSubtotal() {
        return subtotal;
    }
}
>>>>>>> origin/feature/emilio-comprobantes-pruebas
