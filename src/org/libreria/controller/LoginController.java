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
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.libreria.dao.UsuarioDAO;
import org.libreria.manager.SessionContext;
import org.libreria.model.Usuario;

public class LoginController {

    @FXML
    private TextField txtUsername;

    @FXML
    private PasswordField txtPassword;

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    @FXML
    private void handleLogin(ActionEvent event) {
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

        try {
            Usuario usuario = usuarioDAO.autenticar(username, password);

            if (usuario == null) {
                mostrarAlerta(
                        Alert.AlertType.ERROR,
                        "Inicio de sesión",
                        "Usuario o contraseña incorrectos."
                );
                txtPassword.clear();
                txtPassword.requestFocus();
                return;
            }

            SessionContext.iniciarSesion(
                    usuario.getId(),
                    usuario.getUsername(),
                    usuario.getRol()
            );

            abrirMenu(event);

        } catch (Exception e) {
            mostrarAlerta(
                    Alert.AlertType.ERROR,
                    "Error de inicio de sesión",
                    e.getMessage() != null
                            ? e.getMessage()
                            : "Ocurrió un error al iniciar sesión."
            );
        }
    }

    @FXML
    private void handleCerrar(ActionEvent event) {
        Stage stage =
                (Stage) ((Node) event.getSource())
                        .getScene()
                        .getWindow();

        stage.close();
    }

    private void abrirMenu(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "/org/libreria/view/MenuPrincipalDashboardView.fxml"
                    )
            );

            Parent root = loader.load();

            Stage stage =
                    (Stage) ((Node) event.getSource())
                            .getScene()
                            .getWindow();

            stage.setScene(
                    new Scene(root, 1100, 700)
            );

            stage.setMinWidth(980);
            stage.setMinHeight(620);
            stage.setTitle("Librería - Panel Principal");
            stage.centerOnScreen();
            stage.show();

        } catch (IOException | NullPointerException e) {
            mostrarAlerta(
                    Alert.AlertType.ERROR,
                    "Error",
                    "No se pudo abrir el menú principal."
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