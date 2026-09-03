package org.libreria.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import org.libreria.dao.UsuarioDAO;
import org.libreria.model.Usuario;
import org.libreria.system.Main;
import org.libreria.util.SecurityUtil;
import org.libreria.util.Sesion;

public class LoginController implements java.io.Serializable {

    @FXML
    private TextField txtUsuario;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private Button btnIngresar;

    @FXML
    private Label lblMensaje;

    private UsuarioDAO usuarioDAO;

    @FXML
    public void initialize() {
        usuarioDAO = new UsuarioDAO();

        if (lblMensaje != null) {
            lblMensaje.setText("");
        }
    }

    @FXML
    public void handleLogin(ActionEvent evento) {

        String username = txtUsuario.getText().trim();
        String password = txtPassword.getText();

        // Validar campos vacíos
        if (username.isEmpty() || password.isEmpty()) {

            if (lblMensaje != null) {
                lblMensaje.setText("Por favor, complete todos sus datos.");
            }

            return;
        }

        try {

            // Convertir contraseña a SHA-256
            String passwordHash =
                    SecurityUtil.hashSHA256Password(password);

            // Autenticar usuario
            Usuario usuarioIniciado =
                    usuarioDAO.autenticar(username, passwordHash);

            if (usuarioIniciado != null) {

                // Guardar usuario en la sesión
                Sesion.getInstancia()
                        .iniciarSesion(usuarioIniciado);

                if (lblMensaje != null) {
                    lblMensaje.setText("Inicio correcto");
                }

                // Abrir menú principal
                abrirDashboard(usuarioIniciado);

            } else {

                if (lblMensaje != null) {
                    lblMensaje.setText(
                            "Usuario o contraseña incorrectos."
                    );
                }
            }

        } catch (Exception e) {

            e.printStackTrace();

            if (lblMensaje != null) {
                lblMensaje.setText(
                        "Error: " + e.getMessage()
                );
            }
        }
    }

    private void abrirDashboard(Usuario usuario) {

        try {

            FXMLLoader cargador = new FXMLLoader(
                    getClass().getResource(
                            "/org/libreria/view/MenuPrincipalDashboardView.fxml"
                    )
            );

            Parent root = cargador.load();

            Stage escenario = Main.getEscenarioPrincipal();

            escenario.setScene(
                    new Scene(root)
            );

            escenario.setTitle(
                    "Panel Principal - "
                    + usuario.getRol().toUpperCase()
            );

            escenario.show();

        } catch (IOException e) {

            System.err.println(
                    "Error al cargar la vista: "
                    + e.getMessage()
            );

            e.printStackTrace();

            if (lblMensaje != null) {
                lblMensaje.setText(
                        "Error interno al cargar la vista."
                );
            }
        }
    }
}