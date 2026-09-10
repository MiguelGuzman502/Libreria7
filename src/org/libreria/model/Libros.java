package org.libreria.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Libros {
    private String isbn;
    private String titulo;
    private String autor;
    private LocalDate fechaPublicacion;
    private BigDecimal precio;
    private int idCategoria;
    private String nitEditorial;
    private int stock;

    public Libros() {
    }

    public Libros(String isbn, String titulo, LocalDate fechaPublicacion,
            BigDecimal precio, int idCategoria, String nitEditorial, int stock) {
        this.isbn = isbn;
        this.titulo = titulo;
        this.fechaPublicacion = fechaPublicacion;
        this.precio = precio;
        this.idCategoria = idCategoria;
        this.nitEditorial = nitEditorial;
        this.stock = stock;
    }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getAutor() { return autor; }
    public void setAutor(String autor) { this.autor = autor; }
    public LocalDate getFechaPublicacion() { return fechaPublicacion; }
    public void setFechaPublicacion(LocalDate fechaPublicacion) { this.fechaPublicacion = fechaPublicacion; }
    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }
    public int getIdCategoria() { return idCategoria; }
    public void setIdCategoria(int idCategoria) { this.idCategoria = idCategoria; }
    public String getNitEditorial() { return nitEditorial; }
    public void setNitEditorial(String nitEditorial) { this.nitEditorial = nitEditorial; }
    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }
}
