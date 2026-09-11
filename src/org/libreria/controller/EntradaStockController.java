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
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.libreria.dao.LibroDAO;
import org.libreria.dao.impl.LibroDAOImpl;
import org.libreria.model.Libros;

public class EntradaStockController {
    @FXML private TableView<Libros> tablaLibros;
    @FXML private TableColumn<Libros, String> colIsbn;
    @FXML private TableColumn<Libros, String> colTitulo;
    @FXML private TableColumn<Libros, String> colAutor;
    @FXML private TableColumn<Libros, String> colStock;
    @FXML private TextField txtBuscar;
    @FXML private TextField txtCantidad;

    private final LibroDAO libroDAO = new LibroDAOImpl();

    @FXML
    public void initialize() {
        colIsbn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getIsbn()));
        colTitulo.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTitulo()));
        colAutor.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getAutor() == null ? "" : data.getValue().getAutor()));
        colStock.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getStock())));
        cargarLibros();
        txtBuscar.textProperty().addListener((obs, oldValue, newValue) -> filtrar(newValue));
    }

    @FXML
    public void handleAgregar() {
        Libros seleccionado = tablaLibros.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Entrada", "Selecciona un producto.");
            return;
        }

        String texto = txtCantidad.getText().trim();
        int cantidad;
        try {
            cantidad = Integer.parseInt(texto);
        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.WARNING, "Entrada", "Ingresa una cantidad válida.");
            return;
        }

        if (cantidad <= 0) {
            mostrarAlerta(Alert.AlertType.WARNING, "Entrada", "La cantidad debe ser mayor que cero.");
            return;
        }

        try {
            if (!libroDAO.agregarStock(seleccionado.getIsbn(), cantidad)) {
                mostrarAlerta(Alert.AlertType.ERROR, "Entrada", "No se pudo actualizar el stock del producto.");
                return;
            }
            txtCantidad.clear();
            cargarLibros();
            seleccionarPorIsbn(seleccionado.getIsbn());
            mostrarAlerta(Alert.AlertType.INFORMATION, "Entrada registrada", "Se agregaron " + cantidad + " unidades de " + seleccionado.getTitulo() + ".");
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Entrada", e.getMessage());
        }
    }

    @FXML
    public void handleActualizar() {
        cargarLibros();
    }

    @FXML
    public void handleRegresar(Event event) {
        abrirVista("/org/libreria/view/BodegaDashboardView.fxml", event);
    }

    private void cargarLibros() {
        try {
            tablaLibros.setItems(FXCollections.observableArrayList(libroDAO.listar()));
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Entrada", e.getMessage());
        }
    }

    private void filtrar(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            cargarLibros();
            return;
        }
        String criterio = texto.trim();
        try {
            tablaLibros.setItems(FXCollections.observableArrayList(libroDAO.buscarPorTitulo(criterio)));
            if (tablaLibros.getItems().isEmpty()) {
                tablaLibros.setItems(FXCollections.observableArrayList(libroDAO.buscarPorISBN(criterio)));
            }
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Entrada", e.getMessage());
        }
    }

    private void seleccionarPorIsbn(String isbn) {
        for (Libros libro : tablaLibros.getItems()) {
            if (isbn.equals(libro.getIsbn())) {
                tablaLibros.getSelectionModel().select(libro);
                tablaLibros.scrollTo(libro);
                break;
            }
        }
    }

    private void abrirVista(String ruta, Event event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(ruta));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 1100, 700));
            stage.centerOnScreen();
            stage.show();
        } catch (IOException | NullPointerException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo abrir la vista.\n\n" + e.getMessage());
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
