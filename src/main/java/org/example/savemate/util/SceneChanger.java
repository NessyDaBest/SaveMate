package org.example.savemate.util;

import org.example.savemate.util.AppIconLoader;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.animation.FadeTransition;
import javafx.util.Duration;

import java.io.IOException;

public class SceneChanger {

    public static void changeScene(Stage stage, String fxmlPath, String title) {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(200), stage.getScene().getRoot());
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(SceneChanger.class.getResource(fxmlPath));
                Parent root = loader.load();

                Scene scene = new Scene(root);
                stage.setScene(scene);
                AppIconLoader.applyAppIcons(stage);
                stage.setTitle(title);
                stage.setWidth(400);
                stage.setHeight(500);

                // ✨ Fade-in solamente sobre el nuevo contenido
                root.setOpacity(0);
                FadeTransition fadeIn = new FadeTransition(Duration.millis(300), root);
                fadeIn.setFromValue(0.0);
                fadeIn.setToValue(1.0);
                fadeIn.play();

                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        fadeOut.play();
    }
}
