package org.libreria.system;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Main extends Application {

    private static Stage escenarioPrincipal;

    @Override
    public void start(Stage escenarioPrincipal) throws Exception {

        Main.escenarioPrincipal = escenarioPrincipal;

        mostrarLogin();
    }

    private static void mostrarLogin() throws Exception {

        if (escenarioPrincipal != null
                && escenarioPrincipal.isShowing()) {

            escenarioPrincipal.close();
        }

        Stage nuevoEscenario = new Stage();

        /*
         * Ventana transparente para evitar
         * bordes/fondo blanco alrededor del login.
         */
        nuevoEscenario.initStyle(StageStyle.TRANSPARENT);

        FXMLLoader loader =
                new FXMLLoader(
                        Main.class.getResource(
                                "/org/libreria/view/LoginView.fxml"
                        )
                );

        Parent root = loader.load();

        Scene scene = new Scene(root);

        scene.setFill(Color.TRANSPARENT);

        nuevoEscenario.setTitle(
                "Librería - Iniciar Sesión"
        );

        nuevoEscenario.setScene(scene);

        nuevoEscenario.centerOnScreen();
        nuevoEscenario.show();

        escenarioPrincipal = nuevoEscenario;
    }

    public static void cambiarVista(String fxmlPath)
            throws Exception {

        /*
         * Si regresamos al login,
         * utilizamos nuevamente la ventana transparente.
         */
        if (fxmlPath.contains("LoginView")) {

            mostrarLogin();
            return;
        }

        java.net.URL url =
                Main.class.getResource(fxmlPath);

        if (url == null) {

            throw new Exception(
                    "No se encontró el archivo FXML: "
                    + fxmlPath
            );
        }

        Parent root =
                FXMLLoader.load(url);

        if (escenarioPrincipal == null) {

            throw new Exception(
                    "El escenario principal no ha sido inicializado."
            );
        }

        Scene scene =
                escenarioPrincipal.getScene();

        if (scene != null) {

            scene.setRoot(root);

            /*
             * Las demás vistas utilizan
             * fondo normal.
             */
            scene.setFill(Color.WHITE);

        } else {

            scene = new Scene(root);

            escenarioPrincipal.setScene(scene);
        }

        escenarioPrincipal.sizeToScene();
        escenarioPrincipal.centerOnScreen();
    }

    public static Stage getEscenarioPrincipal() {

        return escenarioPrincipal;
    }

    public static void main(String[] args) {

        launch(args);
    }
}