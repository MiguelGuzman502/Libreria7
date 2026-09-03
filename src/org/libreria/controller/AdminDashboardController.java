package org.libreria.controller;

import java.io.IOException;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.libreria.manager.RolPermisos;
import org.libreria.manager.SessionContext;

public class AdminDashboardController {
    @FXML private Label lblUsuarioHeader;

    @FXML
    public void initialize() {
        if (!RolPermisos.puedeAccederAdmin()) return;
        if (SessionContext.sesionActiva()) {
            lblUsuarioHeader.setText("○ " + SessionContext.getUsername() + " (administrador)");
        }
    }

    @FXML private void handleUsuarios(Event event) {
        if (!RolPermisos.puedeGestionarUsuarios()) return;
        abrirVista("/org/libreria/view/GestionUsuariosView.fxml", event);
    }

    @FXML private void handleCambiarPassword(Event event) {
        if (!RolPermisos.puedeCambiarPassword()) return;
        abrirVista("/org/libreria/view/CambiarPasswordView.fxml", event);
    }

    @FXML private void handleCerrarSesion(Event event) {
        SessionContext.cerrarSesion();
        abrirVista("/org/libreria/view/LoginView.fxml", event);
    }

    @FXML private void handleRegresar(Event event) {
        abrirVista("/org/libreria/view/MenuPrincipalDashboardView.fxml", event);
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
            stage.centerOnScreen(); stage.show();
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
        }
    }
}
