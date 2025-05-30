package org.example.savemate.util;

import javafx.animation.FadeTransition;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.example.savemate.controller.MainController;

import java.io.IOException;

public class SceneChanger {

    public static void changeScene(Stage stage, String fxmlPath, String title) {
        if (stage.getScene() != null && stage.getScene().getRoot() != null) {
            FadeTransition fadeOut = new FadeTransition(Duration.millis(200), stage.getScene().getRoot());
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);
            fadeOut.setOnFinished(event -> loadScene(stage, fxmlPath, title));
            fadeOut.play();
        } else {
            loadScene(stage, fxmlPath, title);
        }
    }

    private static void loadScene(Stage stage, String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneChanger.class.getResource(fxmlPath));
            Parent root = loader.load();

            Scene scene = new Scene(root);

            // Añadir CSS según el FXML cargado
            if (fxmlPath.contains("Login") || fxmlPath.contains("Register")) {
                scene.getStylesheets().add(SceneChanger.class.getResource("/org/example/savemate/css/app.css").toExternalForm());
            } else {
                scene.getStylesheets().add(SceneChanger.class.getResource("/org/example/savemate/css/app_main.css").toExternalForm());
            }

            stage.setScene(scene);
            AppIconLoader.applyAppIcons(stage);
            stage.setTitle(title);
            stage.setWidth(720);
            stage.setHeight(500);

            // ⚠️ Llamada genérica a postInitialize si existe
            Object controller = loader.getController();
            try {
                controller.getClass().getMethod("postInitialize", Stage.class).invoke(controller, stage);
            } catch (NoSuchMethodException ignored) {
                // No pasa nada si el controlador no tiene ese método
            }

            root.setOpacity(0);
            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), root);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();

            stage.show();
        } catch (IOException | ReflectiveOperationException e) {
            System.err.println("Error cargando la escena: " + fxmlPath);
            e.printStackTrace();
        }
    }


    public static void openScene(String fxmlPath, String title) {
        Stage newStage = new Stage();
        changeScene(newStage, fxmlPath, title);
    }

    public static void openFXMLPopup(String fxmlPath, String title, ControllerInitializer initializer) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneChanger.class.getResource(fxmlPath));
            Parent root = loader.load();

            Stage popupStage = new Stage();
            popupStage.setTitle(title);
            popupStage.setResizable(false);
            popupStage.setScene(new Scene(root));
            AppIconLoader.applyAppIcons(popupStage);
            popupStage.centerOnScreen();

            initializer.init(loader.getController(), popupStage);

            popupStage.show();
        } catch (IOException e) {
            System.err.println("Error al cargar la escena desde: " + fxmlPath);
            e.printStackTrace();
        }
    }
}