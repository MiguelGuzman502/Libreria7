package org.libreria.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.libreria.model.Usuario;
import org.libreria.util.Sesion;

public class MenuPrincipalDashboardController {

    @FXML private Label lblUsuarioHeader;
    @FXML private Label lblTituloPanel;
    @FXML private Label lblSubtituloPanel;
    @FXML private VBox vboxNavegacion;
    @FXML private FlowPane containerTarjetas;

    @FXML
    public void initialize() {
        Usuario actual = Sesion.getInstancia().getUsuarioActual();
        if (actual != null) {
            lblUsuarioHeader.setText("○ " + actual.getUsername() + " (" + actual.getRol() + ")");
            cargarVistaPorRol();
        } else {
            lblUsuarioHeader.setText("○ Invitado");
        }
    }

    private void cargarVistaPorRol() {
        if (containerTarjetas != null) {
            containerTarjetas.getChildren().clear();
        }

        if (Sesion.getInstancia().esAdmin()) {
            lblTituloPanel.setText("¡Panel Admin!");
            lblSubtituloPanel.setText("Acceso completo a todos los módulos");
            agregarTarjeta("Libros", "Gestionar libros registrados");
            agregarTarjeta("Clientes", "Gestionar clientes");
            agregarTarjeta("Autores", "Gestionar autores");
            agregarTarjeta("Editoriales", "Gestionar editoriales");
            agregarTarjeta("Categorías", "Gestionar categorías");
            agregarTarjeta("Usuarios", "Administrar usuarios");
            agregarTarjeta("Ventas", "Consultar ventas del sistema");

        } else if (Sesion.getInstancia().esCajero()) {
            lblTituloPanel.setText("Panel de Cajero");
            lblSubtituloPanel.setText("Proceso de ventas y consulta de inventario");

            agregarTarjeta("Agregar venta", "Registrar una nueva venta o factura");
            agregarTarjeta("Detalle de ventas", "Líneas de cada venta registrada");
            agregarTarjeta("Lista de ventas", "Consultar ventas registradas");
            agregarTarjeta("Ver inventario", "Consultar el stock disponible actual");

        } else if (Sesion.getInstancia().esBodega()) {
            lblTituloPanel.setText("Panel de Bodega");
            lblSubtituloPanel.setText("Control e inventario de productos");

            agregarTarjeta("Inventario", "Gestión de stock de libros");
            agregarTarjeta("Entradas", "Registrar ingreso de mercancía");
        }
    }

    private void agregarTarjeta(String titulo, String descripcion) {
        VBox card = new VBox(10);
        card.setPrefSize(180, 150);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-border-color: #000000; -fx-border-width: 1px; -fx-background-color: #FFFFFF; -fx-cursor: hand;");

        Label lblTitulo = new Label(titulo);
        lblTitulo.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #000000;");

        Label lblDesc = new Label(descripcion);
        lblDesc.setWrapText(true);
        lblDesc.setAlignment(Pos.CENTER);
        lblDesc.setStyle("-fx-font-size: 11px; -fx-text-fill: #444444;");

        card.getChildren().addAll(lblTitulo, lblDesc);
        
        if (containerTarjetas != null) {
            containerTarjetas.getChildren().add(card);
        }

        Label lblSideItem = new Label(titulo);
        lblSideItem.setStyle("-fx-text-fill: #000000; -fx-cursor: hand; -fx-font-size: 13px;");
        
        if (vboxNavegacion != null) {
            vboxNavegacion.getChildren().add(lblSideItem);
        }
    }

    @FXML
    private void handleCerrarSesion() {
        Sesion.getInstancia().cerrarSesion();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/libreria/view/LoginView.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) lblUsuarioHeader.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Login");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}