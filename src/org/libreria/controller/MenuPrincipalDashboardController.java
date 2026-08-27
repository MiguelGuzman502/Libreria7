package org.libreria.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import org.libreria.system.Main;

public class MenuPrincipalDashboardController {
    @FXML
    private BorderPane rootPane;
    @FXML
    private Button btnClientes;
    @FXML
    private Button btnAutores;
    @FXML
    private Button btnCategorias;
    @FXML
    private Button btnEditoriales;
    @FXML
    private Button btnSalir;
    @FXML
    public void handleClientes(ActionEvent event) {
        System.out.println("Abriendo sección de Clientes...");
    }
    @FXML
    public void handleAutores(ActionEvent event) {
        System.out.println("Abriendo sección de Autores...");
    }
    @FXML
    public void handleCategorias(ActionEvent event) {
        System.out.println("Abriendo sección de Categorías...");
    }
    @FXML
    public void handleEditoriales(ActionEvent event) {
        System.out.println("Abriendo sección de Editoriales...");
    }
    @FXML
    public void handleSalir(ActionEvent event) {
        System.exit(0);
    }
}