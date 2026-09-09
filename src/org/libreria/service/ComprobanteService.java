package org.libreria.service;

import org.libreria.model.DetalleVenta;
import org.libreria.model.Venta;

public class ComprobanteService {

    public String generarTicket(Venta venta) {

        StringBuilder ticket = new StringBuilder();

        ticket.append("================================\n");
        ticket.append("          LIBRERIA 7\n");
        ticket.append("       COMPROBANTE DE VENTA\n");
        ticket.append("================================\n");

        ticket.append("ID DE VENTA: ")
              .append(venta.getIdVenta())
              .append("\n");

        ticket.append("FECHA: ")
              .append(venta.getFecha())
              .append("\n");

        ticket.append("--------------------------------\n");
        ticket.append(String.format("%-15s %5s %8s%n",
                "Producto", "Cant.", "Precio"));

        ticket.append("--------------------------------\n");

        for (DetalleVenta detalle : venta.getDetalles()) {

            ticket.append(String.format(
                    "%-15s %5d %8.2f%n",
                    detalle.getProducto(),
                    detalle.getCantidad(),
                    detalle.getPrecio()
            ));

            ticket.append(String.format(
                    "Subtotal: %.2f%n",
                    detalle.getSubtotal()
            ));
        }

        ticket.append("--------------------------------\n");

        ticket.append(String.format(
                "TOTAL: Q %.2f%n",
                venta.getMontoTotal()
        ));

        ticket.append("================================\n");
        ticket.append("       Gracias por su compra\n");
        ticket.append("================================\n");

        return ticket.toString();
    }
}