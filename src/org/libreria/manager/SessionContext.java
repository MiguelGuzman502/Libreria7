package org.libreria.manager;
public class SessionContext {

    private static String username;
    private static String rol;

    private SessionContext() {
    }

    public static void iniciarSesion(String username, String rol) {
        SessionContext.username = username;
        SessionContext.rol = rol;
    }

    public static String getUsername() {
        return username;
    }

    public static String getRol() {
        return rol;
    }

    public static boolean sesionActiva() {
        return username != null && rol != null;
    }

    public static boolean esAdmin() {
        return rol != null && rol.equalsIgnoreCase("ADMIN");
    }

    public static boolean esCajero() {
        return rol != null && rol.equalsIgnoreCase("CAJERO");
    }

    public static boolean esEmpleado() {
        return rol != null && rol.equalsIgnoreCase("EMPLEADO");
    }

    public static void cerrarSesion() {
        username = null;
        rol = null;
    }
}