package org.libreria.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class ComprobanteController {

    @FXML
    private TextArea txtComprobante;

    public void setComprobante(String comprobante) {
        txtComprobante.setText(comprobante);
    }

    @FXML
    public void handleImprimir() {
    }

    @FXML
    public void handleCerrar() {
        Stage stage = (Stage) txtComprobante.getScene().getWindow();
        stage.close();
    }
}
