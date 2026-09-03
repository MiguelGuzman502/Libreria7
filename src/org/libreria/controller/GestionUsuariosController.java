package org.libreria.controller;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.libreria.manager.RolPermisos;

public class GestionUsuariosController {

    @FXML
    private TextField txtUsername;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private Button btnRegistrar;

    @FXML
    private Button btnCancelar;

    @FXML
    public void initialize() {
        if (!RolPermisos.puedeGestionarUsuarios()) {
            btnRegistrar.setDisable(true);

            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Acceso denegado",
                    "Solo el administrador puede gestionar usuarios."
            );
        }
    }

    @FXML
    private void handleRegistrar(ActionEvent event) {
        if (!RolPermisos.puedeGestionarUsuarios()) {
            accesoDenegado();
            return;
        }

        String username = txtUsername.getText().trim();
        String password = txtPassword.getText();

        if (username.isEmpty() || password.isEmpty()) {
            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Campos requeridos",
                    "Ingrese usuario y contraseña."
            );
            return;
        }
        mostrarAlerta(
                Alert.AlertType.INFORMATION,
                "Gestión de usuarios",
                "Los datos fueron validados correctamente."
        );

        txtUsername.clear();
        txtPassword.clear();
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
                "Solo el administrador puede realizar esta operación."
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