package org.libreria.dao;

import java.util.List;
import org.libreria.model.Venta;

public interface VentaDAO {
    int insertar(Venta venta) throws Exception;
    Venta buscarPorId(int id) throws Exception;
    List<Venta> ventasDelDia() throws Exception;
    List<Venta> listarTodas() throws Exception;
}
