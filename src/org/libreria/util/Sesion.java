package org.libreria.util;

import org.libreria.model.Usuario;

public class Sesion {

    private static Sesion instancia;

    private Usuario usuarioActual;

    private Sesion() {
    }

    public static Sesion getInstancia() {

        if (instancia == null) {
            instancia = new Sesion();
        }

        return instancia;
    }

    public Usuario getUsuarioActual() {
        return usuarioActual;
    }

    public void iniciarSesion(Usuario usuario) {
        this.usuarioActual = usuario;
    }

    public void cerrarSesion() {
        this.usuarioActual = null;
    }

    public boolean haySesionActiva() {
        return usuarioActual != null;
    }

    public boolean esAdmin() {

        return usuarioActual != null
                && "ADMIN".equalsIgnoreCase(
                        usuarioActual.getRol()
                );
    }

    public boolean esCajero() {

        return usuarioActual != null
                && "CAJERO".equalsIgnoreCase(
                        usuarioActual.getRol()
                );
    }

    public boolean esBodega() {

        return usuarioActual != null
                && "BODEGA".equalsIgnoreCase(
                        usuarioActual.getRol()
                );
    }
}