package org.libreria.dao;

import java.util.List;
import org.libreria.model.Libros;

public interface LibroDAO {
    void insertar(Libros libro) throws Exception;
    List<Libros> listar() throws Exception;
    Libros buscarPorISBN(String isbn) throws Exception;
    List<Libros> buscarPorTitulo(String titulo) throws Exception;
    List<Libros> buscarPorAutor(String autor) throws Exception;
    void actualizar(Libros libro) throws Exception;
    void eliminar(String isbn) throws Exception;
    boolean validarStock(String isbn, int cantidad) throws Exception;
    boolean actualizarStock(String isbn, int cantidad) throws Exception;
}