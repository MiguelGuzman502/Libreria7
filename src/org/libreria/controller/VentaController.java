package org.libreria.controller;

import java.io.IOException;
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

import org.libreria.model.CarritoItem;
import org.libreria.model.Libro;

public class VentaController {

    // ==========================================
    // BUSCADOR
    // ==========================================

    @FXML
    private TextField txtBuscar;

    @FXML
    private TableView<Libro> tablaLibros;

    @FXML
    private TableColumn<Libro, String> colId;

    @FXML
    private TableColumn<Libro, String> colIsbn;

    @FXML
    private TableColumn<Libro, String> colTitulo;

    @FXML
    private TableColumn<Libro, String> colAutor;

    @FXML
    private TableColumn<Libro, String> colPrecio;

    @FXML
    private TableColumn<Libro, String> colStock;


    // ==========================================
    // CARRITO
    // ==========================================

    @FXML
    private TableView<CarritoItem> tablaCarrito;

    @FXML
    private TableColumn<CarritoItem, String> colCarritoTitulo;

    @FXML
    private TableColumn<CarritoItem, String> colCarritoPrecio;

    @FXML
    private TableColumn<CarritoItem, String> colCarritoCantidad;

    @FXML
    private TableColumn<CarritoItem, String> colCarritoSubtotal;


    // ==========================================
    // TOTALES
    // ==========================================

    @FXML
    private Label lblSubtotal;

    @FXML
    private Label lblTotal;


    // ==========================================
    // LISTAS
    // ==========================================

    private final ObservableList<Libro> libros =
            FXCollections.observableArrayList();

    private final ObservableList<CarritoItem> carrito =
            FXCollections.observableArrayList();


    // ==========================================
    // INICIALIZAR
    // ==========================================

    @FXML
    public void initialize() {

        configurarTablaLibros();

        configurarTablaCarrito();

        cargarLibrosFicticios();

        tablaLibros.setItems(libros);

        tablaCarrito.setItems(carrito);

        actualizarTotales();
    }


    // ==========================================
    // CONFIGURAR TABLA DE LIBROS
    // ==========================================

    private void configurarTablaLibros() {

        colId.setCellValueFactory(data ->
                new SimpleStringProperty(
                        String.valueOf(
                                data.getValue().getId()
                        )
                )
        );

        colIsbn.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getIsbn()
                )
        );

        colTitulo.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getTitulo()
                )
        );

        colAutor.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getAutor()
                )
        );

        colPrecio.setCellValueFactory(data ->
                new SimpleStringProperty(
                        String.format(
                                "Q %.2f",
                                data.getValue().getPrecio()
                        )
                )
        );

        colStock.setCellValueFactory(data ->
                new SimpleStringProperty(
                        String.valueOf(
                                data.getValue().getStock()
                        )
                )
        );
    }


    // ==========================================
    // CONFIGURAR TABLA CARRITO
    // ==========================================

    private void configurarTablaCarrito() {

        colCarritoTitulo.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue()
                                .getLibro()
                                .getTitulo()
                )
        );

        colCarritoPrecio.setCellValueFactory(data ->
                new SimpleStringProperty(
                        String.format(
                                "Q %.2f",
                                data.getValue()
                                        .getPrecioUnitario()
                        )
                )
        );

        colCarritoCantidad.setCellValueFactory(data ->
                new SimpleStringProperty(
                        String.valueOf(
                                data.getValue().getCantidad()
                        )
                )
        );

        colCarritoSubtotal.setCellValueFactory(data ->
                new SimpleStringProperty(
                        String.format(
                                "Q %.2f",
                                data.getValue().getSubtotal()
                        )
                )
        );
    }


    // ==========================================
    // DATOS FICTICIOS
    // ==========================================

    private void cargarLibrosFicticios() {

        libros.clear();

        libros.add(
                new Libro(
                        1,
                        "978-3-16-148410-0",
                        "Cien Años de Soledad",
                        "Gabriel García Márquez",
                        150.00,
                        10
                )
        );

        libros.add(
                new Libro(
                        2,
                        "978-0-14-044913-6",
                        "Don Quijote",
                        "Miguel de Cervantes",
                        200.00,
                        5
                )
        );

        libros.add(
                new Libro(
                        3,
                        "978-84-376-0494-7",
                        "La Casa de los Espíritus",
                        "Isabel Allende",
                        125.00,
                        8
                )
        );

        libros.add(
                new Libro(
                        4,
                        "978-0-7432-7356-5",
                        "El Código Da Vinci",
                        "Dan Brown",
                        175.00,
                        6
                )
        );

        libros.add(
                new Libro(
                        5,
                        "978-84-206-3374-8",
                        "El Principito",
                        "Antoine de Saint-Exupéry",
                        90.00,
                        15
                )
        );
    }


    // ==========================================
    // BUSCAR
    // ==========================================

    @FXML
    private void handleBuscar() {

        String texto = txtBuscar.getText();

        if (texto == null || texto.trim().isEmpty()) {

            tablaLibros.setItems(libros);

            return;
        }

        String criterio =
                texto.trim().toLowerCase();

        ObservableList<Libro> resultados =
                FXCollections.observableArrayList();

        for (Libro libro : libros) {

            if (
                libro.getTitulo()
                        .toLowerCase()
                        .contains(criterio)

                ||

                libro.getAutor()
                        .toLowerCase()
                        .contains(criterio)

                ||

                libro.getIsbn()
                        .toLowerCase()
                        .contains(criterio)
            ) {

                resultados.add(libro);
            }
        }

        tablaLibros.setItems(resultados);
    }


    // ==========================================
    // LIMPIAR BUSQUEDA
    // ==========================================

    @FXML
    private void handleLimpiarBusqueda() {

        txtBuscar.clear();

        tablaLibros.setItems(libros);
    }


    // ==========================================
    // AGREGAR AL CARRITO
    // ==========================================

    @FXML
    private void handleAgregarCarrito() {

        Libro libroSeleccionado =
                tablaLibros
                        .getSelectionModel()
                        .getSelectedItem();

        if (libroSeleccionado == null) {

            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Seleccionar libro",
                    "Selecciona un libro antes de agregarlo."
            );

            return;
        }

        CarritoItem itemExistente = null;

        for (CarritoItem item : carrito) {

            if (
                item.getLibro().getId()
                ==
                libroSeleccionado.getId()
            ) {

                itemExistente = item;

                break;
            }
        }

        if (itemExistente != null) {

            if (
                itemExistente.getCantidad()
                >=
                libroSeleccionado.getStock()
            ) {

                mostrarAlerta(
                        Alert.AlertType.WARNING,
                        "Stock insuficiente",
                        "No hay más unidades disponibles."
                );

                return;
            }

            itemExistente.setCantidad(
                    itemExistente.getCantidad() + 1
            );

        } else {

            carrito.add(
                    new CarritoItem(
                            libroSeleccionado,
                            1
                    )
            );
        }

        tablaCarrito.refresh();

        actualizarTotales();
    }


    // ==========================================
    // ELIMINAR DEL CARRITO
    // ==========================================

    @FXML
    private void handleEliminarCarrito() {

        CarritoItem seleccionado =
                tablaCarrito
                        .getSelectionModel()
                        .getSelectedItem();

        if (seleccionado == null) {

            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Seleccionar producto",
                    "Selecciona un producto del carrito."
            );

            return;
        }

        carrito.remove(seleccionado);

        actualizarTotales();
    }


    // ==========================================
    // AUMENTAR CANTIDAD
    // ==========================================

    @FXML
    private void handleAumentarCantidad() {

        CarritoItem seleccionado =
                tablaCarrito
                        .getSelectionModel()
                        .getSelectedItem();

        if (seleccionado == null) {

            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Seleccionar producto",
                    "Selecciona un producto del carrito."
            );

            return;
        }

        if (
            seleccionado.getCantidad()
            >=
            seleccionado.getLibro().getStock()
        ) {

            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Stock insuficiente",
                    "No hay más unidades disponibles."
            );

            return;
        }

        seleccionado.setCantidad(
                seleccionado.getCantidad() + 1
        );

        tablaCarrito.refresh();

        actualizarTotales();
    }


    // ==========================================
    // DISMINUIR CANTIDAD
    // ==========================================

    @FXML
    private void handleDisminuirCantidad() {

        CarritoItem seleccionado =
                tablaCarrito
                        .getSelectionModel()
                        .getSelectedItem();

        if (seleccionado == null) {

            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Seleccionar producto",
                    "Selecciona un producto del carrito."
            );

            return;
        }

        if (seleccionado.getCantidad() > 1) {

            seleccionado.setCantidad(
                    seleccionado.getCantidad() - 1
            );

        } else {

            carrito.remove(seleccionado);
        }

        tablaCarrito.refresh();

        actualizarTotales();
    }


    // ==========================================
    // CALCULAR SUBTOTAL Y TOTAL
    // ==========================================

    private void actualizarTotales() {

        double subtotal = 0;

        for (CarritoItem item : carrito) {

            subtotal += item.getSubtotal();
        }

        lblSubtotal.setText(
                String.format(
                        "Q %.2f",
                        subtotal
                )
        );

        lblTotal.setText(
                String.format(
                        "Q %.2f",
                        subtotal
                )
        );
    }


    // ==========================================
    // VACIAR CARRITO
    // ==========================================

    @FXML
    private void handleVaciarCarrito() {

        if (carrito.isEmpty()) {
            return;
        }

        Alert confirmacion =
                new Alert(
                        Alert.AlertType.CONFIRMATION
                );

        confirmacion.setTitle(
                "Vaciar carrito"
        );

        confirmacion.setHeaderText(null);

        confirmacion.setContentText(
                "¿Seguro que deseas eliminar "
                + "todos los productos?"
        );

        Optional<ButtonType> resultado =
                confirmacion.showAndWait();

        if (
            resultado.isPresent()
            &&
            resultado.get() == ButtonType.OK
        ) {

            carrito.clear();

            actualizarTotales();
        }
    }


    // ==========================================
    // REGRESAR
    // ==========================================

    @FXML
    private void handleRegresar(Event event) {

        abrirVista(
                "/org/libreria/view/CajeroDashboardView.fxml",
                event
        );
    }


    // ==========================================
    // MOSTRAR ALERTA
    // ==========================================

    private void mostrarAlerta(
            Alert.AlertType tipo,
            String titulo,
            String mensaje) {

        Alert alert =
                new Alert(tipo);

        alert.setTitle(titulo);

        alert.setHeaderText(null);

        alert.setContentText(mensaje);

        alert.showAndWait();
    }


    // ==========================================
    // ABRIR VISTA
    // ==========================================

    private void abrirVista(
            String ruta,
            Event event) {

        try {

            Parent root =
                    FXMLLoader.load(
                            getClass().getResource(ruta)
                    );

            Stage stage =
                    (Stage)
                    ((Node) event.getSource())
                    .getScene()
                    .getWindow();

            stage.setScene(
                    new Scene(
                            root,
                            1100,
                            700
                    )
            );

            stage.setMinWidth(980);

            stage.setMinHeight(620);

            stage.centerOnScreen();

            stage.show();

        } catch (IOException | NullPointerException e) {

            mostrarAlerta(
                    Alert.AlertType.ERROR,
                    "Error",
                    "No se pudo abrir la vista.\n\n"
                    + e.getMessage()
            );
        }
    }
}

