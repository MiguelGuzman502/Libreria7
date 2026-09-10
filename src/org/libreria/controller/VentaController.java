package org.libreria.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.libreria.dao.LibroDAO;
import org.libreria.dao.VentaTransaccionDAO;
import org.libreria.dao.impl.LibroDAOImpl;
import org.libreria.manager.SessionContext;
import org.libreria.model.CarritoItem;
import org.libreria.model.DetalleVenta;
import org.libreria.model.Libro;
import org.libreria.model.Libros;
import org.libreria.model.Venta;
import org.libreria.service.ComprobanteService;

public class VentaController {
    @FXML private TextField txtBuscar;
    @FXML private TextField txtCuiCliente;
    @FXML private TableView<Libro> tablaLibros;
    @FXML private TableColumn<Libro, String> colId;
    @FXML private TableColumn<Libro, String> colIsbn;
    @FXML private TableColumn<Libro, String> colTitulo;
    @FXML private TableColumn<Libro, String> colAutor;
    @FXML private TableColumn<Libro, String> colPrecio;
    @FXML private TableColumn<Libro, String> colStock;
    @FXML private TableView<CarritoItem> tablaCarrito;
    @FXML private TableColumn<CarritoItem, String> colCarritoTitulo;
    @FXML private TableColumn<CarritoItem, String> colCarritoPrecio;
    @FXML private TableColumn<CarritoItem, String> colCarritoCantidad;
    @FXML private TableColumn<CarritoItem, String> colCarritoSubtotal;
    @FXML private Label lblSubtotal;
    @FXML private Label lblTotal;

    private final ObservableList<Libro> libros = FXCollections.observableArrayList();
    private final ObservableList<CarritoItem> carrito = FXCollections.observableArrayList();
    private final LibroDAO libroDAO = new LibroDAOImpl();
    private final VentaTransaccionDAO transaccionDAO = new VentaTransaccionDAO();
    private final ComprobanteService comprobanteService = new ComprobanteService();

    @FXML
    public void initialize() {
        configurarTablaLibros();
        configurarTablaCarrito();
        tablaLibros.setItems(libros);
        tablaCarrito.setItems(carrito);
        cargarLibros();
        actualizarTotales();
    }

    private void configurarTablaLibros() {
        colId.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(tablaLibros.getItems().indexOf(data.getValue()) + 1)));
        colIsbn.setCellValueFactory(data -> new SimpleStringProperty(valor(data.getValue().getIsbn())));
        colTitulo.setCellValueFactory(data -> new SimpleStringProperty(valor(data.getValue().getTitulo())));
        colAutor.setCellValueFactory(data -> new SimpleStringProperty(valor(data.getValue().getAutor())));
        colPrecio.setCellValueFactory(data -> new SimpleStringProperty(String.format("Q %.2f", data.getValue().getPrecio())));
        colStock.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getStock())));
    }

    private void configurarTablaCarrito() {
        colCarritoTitulo.setCellValueFactory(data -> new SimpleStringProperty(valor(data.getValue().getLibro().getTitulo())));
        colCarritoPrecio.setCellValueFactory(data -> new SimpleStringProperty(String.format("Q %.2f", data.getValue().getPrecioUnitario())));
        colCarritoCantidad.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getCantidad())));
        colCarritoSubtotal.setCellValueFactory(data -> new SimpleStringProperty(String.format("Q %.2f", data.getValue().getSubtotal())));
    }

    private void cargarLibros() {
        try {
            libros.clear();
            for (Libros item : libroDAO.listar()) libros.add(convertir(item));
            tablaLibros.refresh();
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Libros", e.getMessage());
        }
    }

    private Libro convertir(Libros item) {
        return new Libro(0, item.getIsbn(), item.getTitulo(), item.getAutor(), item.getPrecio() == null ? 0 : item.getPrecio().doubleValue(), item.getStock());
    }

    @FXML
    private void handleBuscar() {
        String texto = txtBuscar.getText() == null ? "" : txtBuscar.getText().trim().toLowerCase();
        if (texto.isEmpty()) {
            cargarLibros();
            return;
        }
        try {
            ObservableList<Libro> resultados = FXCollections.observableArrayList();
            Libros exacto = libroDAO.buscarPorISBN(texto);
            if (exacto != null) resultados.add(convertir(exacto));
            if (resultados.isEmpty()) {
                for (Libros item : libroDAO.listar()) {
                    String titulo = valor(item.getTitulo()).toLowerCase();
                    String autor = valor(item.getAutor()).toLowerCase();
                    String isbn = valor(item.getIsbn()).toLowerCase();
                    if (titulo.contains(texto) || autor.contains(texto) || isbn.contains(texto)) resultados.add(convertir(item));
                }
            }
            tablaLibros.setItems(resultados);
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Búsqueda", e.getMessage());
        }
    }

    @FXML
    private void handleLimpiarBusqueda() {
        txtBuscar.clear();
        tablaLibros.setItems(libros);
        tablaLibros.refresh();
    }

    @FXML
    private void handleAgregarCarrito() {
        Libro seleccionado = tablaLibros.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Seleccionar libro", "Selecciona un libro antes de agregarlo.");
            return;
        }
        if (seleccionado.getStock() <= 0) {
            mostrarAlerta(Alert.AlertType.WARNING, "Stock insuficiente", "El libro seleccionado no tiene existencias.");
            return;
        }
        for (CarritoItem item : carrito) {
            if (item.getLibro().getIsbn().equalsIgnoreCase(seleccionado.getIsbn())) {
                if (item.getCantidad() >= seleccionado.getStock()) {
                    mostrarAlerta(Alert.AlertType.WARNING, "Stock insuficiente", "No hay más unidades disponibles.");
                    return;
                }
                item.setCantidad(item.getCantidad() + 1);
                tablaCarrito.refresh();
                actualizarTotales();
                return;
            }
        }
        carrito.add(new CarritoItem(seleccionado, 1));
        actualizarTotales();
    }

    @FXML
    private void handleEliminarCarrito() {
        CarritoItem seleccionado = tablaCarrito.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Seleccionar producto", "Selecciona un producto del carrito.");
            return;
        }
        carrito.remove(seleccionado);
        actualizarTotales();
    }

    @FXML
    private void handleAumentarCantidad() {
        CarritoItem seleccionado = tablaCarrito.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Seleccionar producto", "Selecciona un producto del carrito.");
            return;
        }
        if (seleccionado.getCantidad() >= seleccionado.getLibro().getStock()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Stock insuficiente", "No hay más unidades disponibles.");
            return;
        }
        seleccionado.setCantidad(seleccionado.getCantidad() + 1);
        tablaCarrito.refresh();
        actualizarTotales();
    }

    @FXML
    private void handleDisminuirCantidad() {
        CarritoItem seleccionado = tablaCarrito.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Seleccionar producto", "Selecciona un producto del carrito.");
            return;
        }
        if (seleccionado.getCantidad() <= 1) carrito.remove(seleccionado);
        else seleccionado.setCantidad(seleccionado.getCantidad() - 1);
        tablaCarrito.refresh();
        actualizarTotales();
    }

    @FXML
    private void handleVaciarCarrito() {
        if (carrito.isEmpty()) return;
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Vaciar carrito");
        confirmacion.setHeaderText(null);
        confirmacion.setContentText("¿Seguro que deseas eliminar todos los productos?");
        Optional<ButtonType> resultado = confirmacion.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            carrito.clear();
            actualizarTotales();
        }
    }

    @FXML
    private void handleRegistrarVenta() {
        if (!SessionContext.sesionActiva()) {
            mostrarAlerta(Alert.AlertType.ERROR, "Sesión", "No existe una sesión activa.");
            return;
        }
        if (carrito.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Venta", "Agrega al menos un libro al carrito.");
            return;
        }
        long cui = 0;
        String textoCui = txtCuiCliente.getText() == null ? "" : txtCuiCliente.getText().trim();
        if (!textoCui.isEmpty()) {
            try {
                cui = Long.parseLong(textoCui);
            } catch (NumberFormatException e) {
                mostrarAlerta(Alert.AlertType.WARNING, "CUI", "El CUI debe contener solamente números.");
                return;
            }
        }

        try {
            List<DetalleVenta> detalles = new ArrayList<>();
            BigDecimal total = BigDecimal.ZERO;
            for (CarritoItem item : carrito) {
                if (!libroDAO.validarStock(item.getLibro().getIsbn(), item.getCantidad())) {
                    throw new Exception("Stock insuficiente para: " + item.getLibro().getTitulo());
                }
                BigDecimal subtotal = BigDecimal.valueOf(item.getSubtotal());
                DetalleVenta detalle = new DetalleVenta();
                detalle.setIsbn(item.getLibro().getIsbn());
                detalle.setProducto(item.getLibro().getTitulo());
                detalle.setCantidad(item.getCantidad());
                detalle.setPrecio(item.getPrecioUnitario());
                detalle.setSubtotal(subtotal);
                detalles.add(detalle);
                total = total.add(subtotal);
            }

            Venta venta = new Venta();
            venta.setFecha(LocalDateTime.now());
            venta.setTotal(total);
            venta.setMontoTotal(total.doubleValue());
            venta.setCuiCliente(cui);
            venta.setUsuario(SessionContext.getUsername());
            venta.setDetalles(detalles);

            Venta registrada = transaccionDAO.registrarVentaTransaccional(venta, detalles);
            abrirComprobante(registrada, detalles);
            carrito.clear();
            txtCuiCliente.clear();
            actualizarTotales();
            cargarLibros();
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Venta", e.getMessage());
        }
    }

    private void actualizarTotales() {
        double subtotal = 0;
        for (CarritoItem item : carrito) subtotal += item.getSubtotal();
        lblSubtotal.setText(String.format("Q %.2f", subtotal));
        lblTotal.setText(String.format("Q %.2f", subtotal));
    }

    private void abrirComprobante(Venta venta, List<DetalleVenta> detalles) throws IOException {
        venta.setDetalles(detalles);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/libreria/view/ComprobanteView.fxml"));
        Parent root = loader.load();
        ComprobanteController controller = loader.getController();
        controller.setComprobante(comprobanteService.generarTicket(venta));
        Stage stage = new Stage();
        stage.setTitle("Comprobante de venta");
        stage.setScene(new Scene(root, 620, 700));
        stage.showAndWait();
    }

    @FXML
    private void handleRegresar(Event event) {
        abrirVista("/org/libreria/view/CajeroDashboardView.fxml", event);
    }

    private void abrirVista(String ruta, Event event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(ruta));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 1100, 700));
            stage.setMinWidth(980);
            stage.setMinHeight(620);
            stage.centerOnScreen();
            stage.show();
        } catch (IOException | NullPointerException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo abrir la vista.\n\n" + e.getMessage());
        }
    }

    private String valor(String valor) {
        return valor == null ? "" : valor;
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje == null ? "Ocurrió un error." : mensaje);
        alert.showAndWait();
    }
}
