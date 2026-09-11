package org.libreria.controller;

import java.io.IOException;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import org.libreria.dao.LibroDAO;
import org.libreria.dao.impl.LibroDAOImpl;
import org.libreria.model.Libros;

public class InventarioController {
    @FXML private TableView<Libros> tablaInventario;
    @FXML private TableColumn<Libros, String> colIsbn;
    @FXML private TableColumn<Libros, String> colTitulo;
    @FXML private TableColumn<Libros, String> colAutor;
    @FXML private TableColumn<Libros, String> colPrecio;
    @FXML private TableColumn<Libros, String> colStock;

    private final LibroDAO libroDAO = new LibroDAOImpl();

    @FXML
    public void initialize() {
        colIsbn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getIsbn()));
        colTitulo.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTitulo()));
        colAutor.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getAutor() == null ? "" : data.getValue().getAutor()));
        colPrecio.setCellValueFactory(data -> new SimpleStringProperty(String.format("Q %.2f", data.getValue().getPrecio() == null ? 0 : data.getValue().getPrecio().doubleValue())));
        colStock.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getStock())));
        cargar();
    }

    @FXML public void handleActualizar() { cargar(); }
    @FXML public void handleRegresar(Event event) { abrirVista("/org/libreria/view/BodegaDashboardView.fxml", event); }

    private void cargar() {
        try {
            tablaInventario.setItems(FXCollections.observableArrayList(libroDAO.listar()));
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Inventario", e.getMessage());
        }
    }

    private void abrirVista(String ruta, Event event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(ruta));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 1100, 700));
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", e.getMessage());
        }
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
