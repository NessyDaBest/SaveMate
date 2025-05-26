package org.example.savemate;

import org.example.savemate.util.AppIconLoader;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/Login.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);

        primaryStage.setTitle("SaveMate - Inicio de sesión");
        primaryStage.setScene(scene);

        // Cargar varios tamaños de icono
        AppIconLoader.applyAppIcons(primaryStage);

        // Tamaño fijo
        primaryStage.setWidth(400);
        primaryStage.setHeight(500);
        primaryStage.setMinWidth(400);
        primaryStage.setMinHeight(500);

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
