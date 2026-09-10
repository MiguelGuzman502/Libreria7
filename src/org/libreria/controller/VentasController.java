package org.libreria.controller;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.libreria.dao.DetalleVentaDAO;
import org.libreria.dao.VentaDAO;
import org.libreria.dao.impl.DetalleVentaDAOImpl;
import org.libreria.dao.impl.VentaDAOImpl;
import org.libreria.model.DetalleVenta;
import org.libreria.model.Venta;

public class VentasController {
    @FXML private TableView<Venta> tablaVentas;
    @FXML private TableColumn<Venta, String> colVenta;
    @FXML private TableColumn<Venta, String> colFecha;
    @FXML private TableColumn<Venta, String> colCui;
    @FXML private TableColumn<Venta, String> colTotal;
    @FXML private TableView<DetalleVenta> tablaDetalles;
    @FXML private TableColumn<DetalleVenta, String> colDetalleIsbn;
    @FXML private TableColumn<DetalleVenta, String> colDetalleProducto;
    @FXML private TableColumn<DetalleVenta, String> colDetalleCantidad;
    @FXML private TableColumn<DetalleVenta, String> colDetalleSubtotal;
    @FXML private TextField txtIdVenta;
    @FXML private Label lblTotalDia;

    private final VentaDAO ventaDAO = new VentaDAOImpl();
    private final DetalleVentaDAO detalleDAO = new DetalleVentaDAOImpl();
    private final DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FXML
    public void initialize() {
        configurarTablas();
        cargarVentas();
        tablaVentas.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> cargarDetalles(newValue));
    }

    private void configurarTablas() {
        colVenta.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getId())));
        colFecha.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getFecha() == null ? "" : data.getValue().getFecha().format(formato)));
        colCui.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCuiCliente() <= 0 ? "Sin CUI" : String.valueOf(data.getValue().getCuiCliente())));
        colTotal.setCellValueFactory(data -> new SimpleStringProperty(String.format("Q %.2f", data.getValue().getTotal() == null ? 0 : data.getValue().getTotal().doubleValue())));
        colDetalleIsbn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getIsbn()));
        colDetalleProducto.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getProducto() == null ? "" : data.getValue().getProducto()));
        colDetalleCantidad.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getCantidad())));
        colDetalleSubtotal.setCellValueFactory(data -> new SimpleStringProperty(String.format("Q %.2f", data.getValue().getSubtotal() == null ? 0 : data.getValue().getSubtotal().doubleValue())));
    }

    @FXML
    public void handleActualizar() { cargarVentas(); }

    @FXML
    public void handleBuscar() {
        String texto = txtIdVenta.getText() == null ? "" : txtIdVenta.getText().trim();
        if (texto.isEmpty()) {
            cargarVentas();
            return;
        }
        try {
            int id = Integer.parseInt(texto);
            Venta venta = ventaDAO.buscarPorId(id);
            tablaVentas.setItems(venta == null ? FXCollections.observableArrayList() : FXCollections.observableArrayList(venta));
            cargarDetalles(venta);
        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.WARNING, "Consulta", "El ID de venta debe ser numérico.");
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Consulta", e.getMessage());
        }
    }

    private void cargarVentas() {
        try {
            var ventas = ventaDAO.ventasDelDia();
            tablaVentas.setItems(FXCollections.observableArrayList(ventas));
            double total = 0;
            for (Venta venta : ventas) if (venta.getTotal() != null) total += venta.getTotal().doubleValue();
            lblTotalDia.setText(String.format("Total del día: Q %.2f", total));
            if (!ventas.isEmpty()) tablaVentas.getSelectionModel().selectFirst();
            else tablaDetalles.getItems().clear();
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Ventas", e.getMessage());
        }
    }

    private void cargarDetalles(Venta venta) {
        if (venta == null) {
            tablaDetalles.getItems().clear();
            return;
        }
        try {
            tablaDetalles.setItems(FXCollections.observableArrayList(detalleDAO.listarPorVenta(venta.getId())));
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Detalle", e.getMessage());
        }
    }

    @FXML
    public void handleRegresar(Event event) { abrirVista("/org/libreria/view/CajeroDashboardView.fxml", event); }

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
