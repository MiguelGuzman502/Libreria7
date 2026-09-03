package org.libreria.manager;
public class RolPermisos {

    private RolPermisos() {
    }

    public static boolean puedeAccederAdmin() {
        return SessionContext.esAdmin();
    }

    public static boolean puedeAccederBodega() {
        return SessionContext.esAdmin()
                || SessionContext.esEmpleado();
    }

    public static boolean puedeAccederCajero() {
        return SessionContext.esAdmin()
                || SessionContext.esCajero();
    }

    public static boolean puedeGestionarUsuarios() {
        return SessionContext.esAdmin();
    }

    public static boolean puedeCambiarPassword() {
        return SessionContext.sesionActiva();
    }

    public static boolean puedeCerrarSesion() {
        return SessionContext.sesionActiva();
    }
}