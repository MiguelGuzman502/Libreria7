package org.libreria.system;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    private static Stage escenarioPrincipal;

    @Override
    public void start(Stage escenarioPrincipal) throws Exception {
        Main.escenarioPrincipal = escenarioPrincipal;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/libreria/view/LoginView.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root, 520, 650);
        escenarioPrincipal.setTitle("Librería - Iniciar Sesión");
        escenarioPrincipal.setScene(scene);
        escenarioPrincipal.setMinWidth(520);
        escenarioPrincipal.setMinHeight(650);
        escenarioPrincipal.centerOnScreen();
        escenarioPrincipal.show();
    }

    public static void cambiarVista(String fxmlPath) throws Exception {
        java.net.URL url = Main.class.getResource(fxmlPath);
        if (url == null) throw new Exception("No se encontró el archivo FXML: " + fxmlPath);
        Parent root = FXMLLoader.load(url);
        if (escenarioPrincipal == null) throw new Exception("El escenario principal no ha sido inicializado.");
        escenarioPrincipal.setScene(new Scene(root));
    }

    public static Stage getEscenarioPrincipal() { return escenarioPrincipal; }

    public static void main(String[] args) { launch(args); }
}
