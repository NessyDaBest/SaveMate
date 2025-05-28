package org.example.savemate.util;

import javafx.animation.FadeTransition;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class SceneChanger {

    public static void changeScene(Stage stage, String fxmlPath, String title) {
        if (stage.getScene() != null && stage.getScene().getRoot() != null) {
            // Si ya tiene escena, hacer fade-out
            FadeTransition fadeOut = new FadeTransition(Duration.millis(200), stage.getScene().getRoot());
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);
            fadeOut.setOnFinished(event -> loadScene(stage, fxmlPath, title));
            fadeOut.play();
        } else {
            // Si no tiene escena, cargar directamente
            loadScene(stage, fxmlPath, title);
        }
    }

    private static void loadScene(Stage stage, String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneChanger.class.getResource(fxmlPath));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            stage.setScene(scene);
            AppIconLoader.applyAppIcons(stage);
            stage.setTitle(title);
            stage.setWidth(400);
            stage.setHeight(500);

            // Fade-in animado
            root.setOpacity(0);
            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), root);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();

            stage.show();
        } catch (IOException e) {
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
            e.printStackTrace();
        }
    }
}