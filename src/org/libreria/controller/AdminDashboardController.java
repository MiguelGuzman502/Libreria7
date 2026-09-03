package org.libreria.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.libreria.model.Usuario;
import org.libreria.util.Sesion;

public class AdminDashboardController {

    @FXML
    private Label lblUsuarioHeader;

    @FXML
    public void initialize() {

        Usuario actual = Sesion.getInstancia().getUsuarioActual();

        if (actual != null) {
            lblUsuarioHeader.setText(
                "○ " + actual.getUsername() +
                " (" + actual.getRol() + ")"
            );
        } else {
            lblUsuarioHeader.setText("○ Invitado");
        }
    }

    @FXML
    private void handleCerrarSesion() {

        Sesion.getInstancia().cerrarSesion();

        try {

            FXMLLoader loader = new FXMLLoader(
                getClass().getResource(
                    "/org/libreria/view/LoginView.fxml"
                )
            );

            Scene scene = new Scene(loader.load());

            Stage stage = (Stage) lblUsuarioHeader
                    .getScene()
                    .getWindow();

            stage.setScene(scene);
            stage.setTitle("Login");
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}