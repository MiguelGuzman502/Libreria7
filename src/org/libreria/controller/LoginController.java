package org.libreria.controller;
 
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import org.libreria.dao.UsuarioDAO;
import org.libreria.model.Usuario;
import org.libreria.util.Sesion;
 
public class LoginController {
 
    @FXML
    private TextField txtUsuario;
 
    @FXML
    private PasswordField txtPassword;
 
    @FXML
    private Button btnIngresar;
 
    @FXML
    private Label lblMensaje;
 
    private UsuarioDAO usuarioDAO = new UsuarioDAO();
 
    @FXML
    private void handleLogin() {
        String username = txtUsuario.getText().trim();
        String password = txtPassword.getText().trim();
 
        if (username.isEmpty() || password.isEmpty()) {
            lblMensaje.setText("Por favor complete todos los campos.");
            return;
        }
 
        try {
            // Llama a tu Backend
            Usuario usuario = usuarioDAO.autenticar(username, password);
 
            // Almacena la sesión
            Sesion.getInstancia().iniciarSesion(usuario);
 
            // Redirige al Dashboard
            abrirDashboard();
 
        } catch (Exception e) {
            // Muestra mensaje de contraseña incorrecta, usuario inactivo o inexistente
            lblMensaje.setText(e.getMessage());
        }
    }
 
    private void abrirDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/libreria/view/MenuPrincipalDashboardView.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) btnIngresar.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Dashboard Principal");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            lblMensaje.setText("Error al cargar la vista principal.");
        }
    }
}
