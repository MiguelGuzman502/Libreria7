package org.libreria.model;

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
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

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
