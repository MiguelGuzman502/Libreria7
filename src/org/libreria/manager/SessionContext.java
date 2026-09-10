package org.libreria.manager;

public class SessionContext {

    private static int idUsuario;
    private static String username;
    private static String rol;

    private SessionContext() {
    }

    public static void iniciarSesion(String username, String rol) {
        SessionContext.idUsuario = 0;
        SessionContext.username = username;
        SessionContext.rol = rol;
    }

    public static void iniciarSesion(int idUsuario, String username, String rol) {
        SessionContext.idUsuario = idUsuario;
        SessionContext.username = username;
        SessionContext.rol = rol;
    }

    public static int getIdUsuario() {
        return idUsuario;
    }

    public static String getUsername() {
        return username;
    }

    public static String getRol() {
        return rol;
    }

    public static boolean sesionActiva() {
        return username != null && !username.isEmpty()
                && rol != null && !rol.isEmpty();
    }

    public static boolean esAdmin() {
        return rol != null && rol.equalsIgnoreCase("admin");
    }

    public static boolean esCajero() {
        return rol != null && rol.equalsIgnoreCase("cajero");
    }

    public static boolean esEmpleado() {
        return rol != null && rol.equalsIgnoreCase("empleado");
    }

    public static void cerrarSesion() {
        idUsuario = 0;
        username = null;
        rol = null;
    }
}