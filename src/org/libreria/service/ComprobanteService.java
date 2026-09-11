package org.libreria.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.libreria.model.DetalleVenta;
import org.libreria.model.Venta;

public class ComprobanteService {

    private final DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public String generarTicket(Venta venta) {
        StringBuilder ticket = new StringBuilder();
        String factura = String.format("7ac%05d-000", venta.getIdVenta());
        String fecha = venta.getFecha() == null ? LocalDate.now().format(formatoFecha) : venta.getFecha().toLocalDate().format(formatoFecha);
        String devolucion = (venta.getFecha() == null ? LocalDate.now() : venta.getFecha().toLocalDate()).plusDays(10).format(formatoFecha);

        ticket.append("Libreria7\n");
        ticket.append("4xxx-xxxx\n");
        ticket.append("libreria7@gmail.com\n\n");
        ticket.append("Factura simpl: ").append(factura).append("\n");
        ticket.append("Fecha: ").append(fecha).append("\n\n");
        ticket.append("Metodo de pago: efectivo\n\n");
        ticket.append(String.format("%-25s %4s %11s %11s%n", "Articulo", "Ud", "Precio", "Total"));
        ticket.append("------------------------------------------------------\n");

        for (DetalleVenta detalle : venta.getDetalles()) {
            double precio = detalle.getPrecio();
            double total = detalle.getSubtotal() == null ? precio * detalle.getCantidad() : detalle.getSubtotal().doubleValue();
            ticket.append(String.format("%-25s %4d Q%9.2f Q%9.2f%n", ajustar(detalle.getProducto()), detalle.getCantidad(), precio, total));
            ticket.append("\n");
        }

        ticket.append("------------------------------------------------------\n");
        ticket.append(String.format("Total =%39sQ%.2f%n", "", venta.getMontoTotal()));
        ticket.append("\n");
        ticket.append("\n");
        ticket.append("              El periodo de\n");
        ticket.append("          devoluciones termina\n");
        ticket.append("             el dia ").append(devolucion).append("\n");

        return ticket.toString();
    }

    private String ajustar(String texto) {
        if (texto == null) {
            return "";
        }
        return texto.length() <= 25 ? texto : texto.substring(0, 22) + "...";
    }
}
