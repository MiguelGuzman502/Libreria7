package org.libreria.controller;

import javafx.fxml.FXML;
import javafx.print.PrinterJob;
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
        PrinterJob job = PrinterJob.createPrinterJob();

        if (job != null && job.showPrintDialog(txtComprobante.getScene().getWindow())) {
            boolean success = job.printPage(txtComprobante);

            if (success) {
                job.endJob();
            }
        }
    }

    @FXML
    public void handleCerrar() {
        Stage stage = (Stage) txtComprobante.getScene().getWindow();
        stage.close();
    }
}