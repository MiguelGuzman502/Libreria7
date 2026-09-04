package org.libreria.controller;

import java.io.IOException;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import org.libreria.dao.UsuarioDAO;
import org.libreria.manager.RolPermisos;
import org.libreria.model.Usuario;

public class GestionUsuariosController {

    @FXML
    private TextField txtUsername;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private PasswordField txtConfirmarPassword;

    @FXML
    private ComboBox<String> cmbRol;

    @FXML
    private Button btnRegistrar;

    @FXML
    private Button btnLimpiar;

    @FXML
    private Button btnRegresar;

    @FXML
    private ListView<String> lstUsuarios;


    private final UsuarioDAO usuarioDAO =
            new UsuarioDAO();


    // =====================================================
    // INICIALIZAR
    // =====================================================

    @FXML
    public void initialize() {

        cmbRol.getItems().addAll(
                "admin",
                "empleado",
                "cajero"
        );

        cmbRol.getSelectionModel().selectFirst();


        if (!RolPermisos.puedeGestionarUsuarios()) {

            btnRegistrar.setDisable(true);

            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Acceso denegado",
                    "Solo el administrador puede registrar usuarios."
            );

            return;
        }


        // Cargar usuarios existentes
        cargarUsuarios();
    }


    // =====================================================
    // CARGAR USUARIOS
    // =====================================================

    private void cargarUsuarios() {

        try {

            List<Usuario> usuarios =
                    usuarioDAO.listarUsuarios();

            ObservableList<String> lista =
                    FXCollections.observableArrayList();


            for (Usuario usuario : usuarios) {

                String estado =
                        usuario.isActivo()
                        ? "Activo"
                        : "Inactivo";

                String informacion =
                        "👤  "
                        + usuario.getUsername()
                        + "     |     Rol: "
                        + usuario.getRol().toUpperCase()
                        + "     |     "
                        + estado;

                lista.add(informacion);
            }


            lstUsuarios.setItems(lista);


        } catch (Exception e) {

            mostrarAlerta(
                    Alert.AlertType.ERROR,
                    "Error",
                    "No se pudieron cargar los usuarios.\n\n"
                    + e.getMessage()
            );
        }
    }


    // =====================================================
    // REGISTRAR USUARIO
    // =====================================================

    @FXML
    private void handleRegistrar(ActionEvent event) {

        if (!RolPermisos.puedeGestionarUsuarios()) {

            mostrarAlerta(
                    Alert.AlertType.ERROR,
                    "Acceso denegado",
                    "No tienes permisos para registrar usuarios."
            );

            return;
        }


        String username =
                txtUsername.getText().trim();

        String password =
                txtPassword.getText();

        String confirmarPassword =
                txtConfirmarPassword.getText();

        String rol =
                cmbRol.getValue();


        // ==============================
        // VALIDACIONES
        // ==============================

        if (username.isEmpty()
                || password.isEmpty()
                || confirmarPassword.isEmpty()
                || rol == null) {

            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Campos requeridos",
                    "Debe completar todos los campos."
            );

            return;
        }


        if (username.length() < 3) {

            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Usuario inválido",
                    "El nombre de usuario debe tener al menos 3 caracteres."
            );

            return;
        }


        if (username.length() > 50) {

            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Usuario inválido",
                    "El nombre de usuario no puede superar los 50 caracteres."
            );

            return;
        }


        if (password.length() < 6) {

            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Contraseña inválida",
                    "La contraseña debe tener al menos 6 caracteres."
            );

            return;
        }


        if (!password.equals(confirmarPassword)) {

            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Contraseñas",
                    "Las contraseñas no coinciden."
            );

            return;
        }


        // ==============================
        // REGISTRO
        // ==============================

        try {

            usuarioDAO.registrarUsuario(
                    username,
                    password,
                    rol
            );


            mostrarAlerta(
                    Alert.AlertType.INFORMATION,
                    "Usuario registrado",
                    "El usuario '"
                    + username
                    + "' fue registrado correctamente."
            );


            // Limpiar formulario
            limpiarFormulario();


            // Actualizar lista
            cargarUsuarios();


        } catch (Exception e) {

            mostrarAlerta(
                    Alert.AlertType.ERROR,
                    "Error al registrar",
                    e.getMessage()
            );
        }
    }


    // =====================================================
    // LIMPIAR
    // =====================================================

    @FXML
    private void handleLimpiar(ActionEvent event) {

        limpiarFormulario();
    }


    private void limpiarFormulario() {

        txtUsername.clear();

        txtPassword.clear();

        txtConfirmarPassword.clear();

        cmbRol.getSelectionModel()
                .selectFirst();

        txtUsername.requestFocus();
    }


    // =====================================================
    // REGRESAR
    // =====================================================

    @FXML
    private void handleRegresar(ActionEvent event) {

        abrirVista(
                "/org/libreria/view/MenuPrincipalDashboardView.fxml",
                event
        );
    }


    // =====================================================
    // ABRIR VISTA
    // =====================================================

    private void abrirVista(
            String ruta,
            ActionEvent event) {

        try {

            FXMLLoader loader =
                    new FXMLLoader(
                            getClass().getResource(ruta)
                    );

            Parent root =
                    loader.load();

            Stage stage =
                    (Stage)
                    ((Node) event.getSource())
                            .getScene()
                            .getWindow();

            stage.setScene(
                    new Scene(root)
            );

            stage.show();

        } catch (IOException |
                 NullPointerException e) {

            mostrarAlerta(
                    Alert.AlertType.ERROR,
                    "Error",
                    "No se pudo abrir la vista:\n"
                    + ruta
            );
        }
    }


    // =====================================================
    // ALERTAS
    // =====================================================

    private void mostrarAlerta(
            Alert.AlertType tipo,
            String titulo,
            String mensaje) {

        Alert alert =
                new Alert(tipo);

        alert.setTitle(titulo);

        alert.setHeaderText(null);

        alert.setContentText(mensaje);

        alert.showAndWait();
    }
}