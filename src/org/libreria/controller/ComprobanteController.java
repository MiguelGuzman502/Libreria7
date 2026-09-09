package org.libreria.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class ComprobanteController {

    @FXML
    private TextArea txtComprobante;

    @FXML
    private Button btnCerrar;

    public void setComprobante(String comprobante) {
        txtComprobante.setText(comprobante);
    }

    @FXML
    private void handleCerrar() {
        Stage stage = (Stage) btnCerrar.getScene().getWindow();
        stage.close();
    }
}