package org.libreria.model;

public class Usuario {

    private int id;
    private String usrname;
    private String password;
    private String rol;
    private boolean activo;

    public Usuario() {
    }

    public Usuario(int id, String usrname, String password, String rol, boolean activo) {
        this.id = id;
        this.usrname = usrname;
        this.password = password;
        this.rol = rol;
        this.activo = activo;
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsrname() {
        return usrname;
    }

    public void setUsrname(String usrname) {
        this.usrname = usrname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}
