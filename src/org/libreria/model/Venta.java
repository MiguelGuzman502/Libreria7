package org.libreria.model;

<<<<<<< HEAD
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Venta {

    private int id;
    private LocalDateTime fecha;
    private BigDecimal total;
    private long cuiCliente;
    private String usuario;

    public Venta() {
    }

    public Venta(int id, LocalDateTime fecha, BigDecimal total,
            long cuiCliente, String usuario) {

        this.id = id;
        this.fecha = fecha;
        this.total = total;
        this.cuiCliente = cuiCliente;
        this.usuario = usuario;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
=======
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
>>>>>>> origin/feature/emilio-comprobantes-pruebas
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

<<<<<<< HEAD
    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public long getCuiCliente() {
        return cuiCliente;
    }

    public void setCuiCliente(long cuiCliente) {
        this.cuiCliente = cuiCliente;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }
}
=======
    public double getMontoTotal() {
        return montoTotal;
    }

    public List<DetalleVenta> getDetalles() {
        return detalles;
    }
}
>>>>>>> origin/feature/emilio-comprobantes-pruebas
