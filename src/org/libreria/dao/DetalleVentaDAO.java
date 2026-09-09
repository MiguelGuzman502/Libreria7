package org.libreria.dao;

import java.util.List;
import org.libreria.model.DetalleVenta;

public interface DetalleVentaDAO {

    void insertarMasivo(int idVenta,
            List<DetalleVenta> detalles) throws Exception;
}
