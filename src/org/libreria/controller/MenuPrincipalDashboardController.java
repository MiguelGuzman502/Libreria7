package org.libreria.controller;

import java.io.IOException;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.libreria.manager.RolPermisos;
import org.libreria.manager.SessionContext;

public class MenuPrincipalDashboardController {
    @FXML private Label lblUsuario;
    @FXML private Label lblRol;
    @FXML private Label lblUsuarioHeader;
    @FXML private Button btnAdmin;
    @FXML private Button btnBodega;
    @FXML private Button btnCajero;
    @FXML private Button btnGestionUsuarios;
    @FXML private Button btnCambiarPassword;

    @FXML
    public void initialize() {
        if (!SessionContext.sesionActiva()) return;
        lblUsuario.setText("Usuario: " + SessionContext.getUsername());
        lblRol.setText("Rol: " + SessionContext.getRol());
        lblUsuarioHeader.setText("○ " + SessionContext.getUsername() + " (" + SessionContext.getRol().toLowerCase() + ")");
        configurarPermisos();
    }

    private void configurarPermisos() {
        configurarBoton(btnAdmin, RolPermisos.puedeAccederAdmin());
        configurarBoton(btnBodega, RolPermisos.puedeAccederBodega());
        configurarBoton(btnCajero, RolPermisos.puedeAccederCajero());
        configurarBoton(btnGestionUsuarios, RolPermisos.puedeGestionarUsuarios());
        configurarBoton(btnCambiarPassword, RolPermisos.puedeCambiarPassword());
    }

    private void configurarBoton(Button boton, boolean permitido) {
        if (boton != null) {
            boton.setVisible(permitido);
            boton.setManaged(permitido);
        }
    }

    @FXML private void handleInicio(Event event) { /* Ya estamos en inicio. */ }

    @FXML private void handleAdmin(Event event) {
        if (!RolPermisos.puedeAccederAdmin()) { accesoDenegado(); return; }
        abrirVista("/org/libreria/view/AdminDashboardView.fxml", event);
    }

    @FXML private void handleBodega(Event event) {
        if (!RolPermisos.puedeAccederBodega()) { accesoDenegado(); return; }
        abrirVista("/org/libreria/view/BodegaDashboardView.fxml", event);
    }

    @FXML private void handleCajero(Event event) {
        if (!RolPermisos.puedeAccederCajero()) { accesoDenegado(); return; }
        abrirVista("/org/libreria/view/CajeroDashboardView.fxml", event);
    }

    @FXML private void handleGestionUsuarios(Event event) {
        if (!RolPermisos.puedeGestionarUsuarios()) { accesoDenegado(); return; }
        abrirVista("/org/libreria/view/GestionUsuariosView.fxml", event);
    }

    @FXML private void handleCambiarPassword(Event event) {
        if (!RolPermisos.puedeCambiarPassword()) { accesoDenegado(); return; }
        abrirVista("/org/libreria/view/CambiarPasswordView.fxml", event);
    }

    @FXML private void handleCerrarSesion(Event event) {
        SessionContext.cerrarSesion();
        abrirVista("/org/libreria/view/LoginView.fxml", event);
    }

    private void abrirVista(String ruta, Event event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(ruta));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            if (ruta.endsWith("LoginView.fxml")) {
                stage.setScene(new Scene(root, 520, 650));
                stage.setMinWidth(520); stage.setMinHeight(650);
            } else {
                stage.setScene(new Scene(root, 1100, 700));
                stage.setMinWidth(980); stage.setMinHeight(620);
            }
            stage.centerOnScreen();
            stage.show();
        } catch (IOException | NullPointerException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo abrir la vista:\n\n" + ruta + "\n\n" + e.getMessage());
        }
    }

    private void accesoDenegado() {
        mostrarAlerta(Alert.AlertType.WARNING, "Acceso denegado", "No tienes permisos para acceder a este módulo.");
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo); alert.setHeaderText(null); alert.setContentText(mensaje); alert.showAndWait();
    }
}
