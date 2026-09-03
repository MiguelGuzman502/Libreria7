package org.libreria.controller;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;
import org.libreria.manager.SessionContext;
import org.libreria.manager.RolPermisos;

public class CambiarPasswordController {

    @FXML
    private PasswordField txtPasswordActual;

    @FXML
    private PasswordField txtPasswordNueva;

    @FXML
    private PasswordField txtConfirmarPassword;
    
    @FXML
    public void initialize() {
        if (!RolPermisos.puedeCambiarPassword()) {
            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Sesión",
                    "No existe una sesión activa."
            );
        }
    }

    @FXML
    private void handleCambiarPassword(ActionEvent event) {
        if (!RolPermisos.puedeCambiarPassword()) {
            accesoDenegado();
            return;
        }

        String actual = txtPasswordActual.getText();
        String nueva = txtPasswordNueva.getText();
        String confirmacion = txtConfirmarPassword.getText();

        if (actual.isEmpty()
                || nueva.isEmpty()
                || confirmacion.isEmpty()) {
            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Campos requeridos",
                    "Debe completar todos los campos."
            );
            return;
        }

        if (!nueva.equals(confirmacion)) {
            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Contraseñas",
                    "La nueva contraseña y la confirmación no coinciden."
            );
            return;
        }

        if (nueva.length() < 6) {
            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Contraseña",
                    "La nueva contraseña debe tener al menos 6 caracteres."
            );
            return;
        }

        String username = SessionContext.getUsername();

        mostrarAlerta(
                Alert.AlertType.INFORMATION,
                "Contraseña",
                "La contraseña fue validada correctamente para el usuario: " + username
        );

        txtPasswordActual.clear();
        txtPasswordNueva.clear();
        txtConfirmarPassword.clear();
    }

    @FXML
    private void handleCancelar(ActionEvent event) {
        abrirVista(
                "/org/libreria/view/MenuPrincipalDashboardView.fxml",
                event
        );
    }

    private void accesoDenegado() {
        mostrarAlerta(
                Alert.AlertType.WARNING,
                "Acceso denegado",
                "Debe iniciar sesión para cambiar la contraseña."
        );
    }

    private void abrirVista(String ruta, ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(
                    getClass().getResource(ruta)
            );

            Stage stage = (Stage)
                    ((Node) event.getSource())
                            .getScene()
                            .getWindow();

            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException | NullPointerException e) {
            mostrarAlerta(
                    Alert.AlertType.ERROR,
                    "Error",
                    "No se pudo abrir la vista."
            );
        }
    }

    private void mostrarAlerta(
            Alert.AlertType tipo,
            String titulo,
            String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}