package org.example.savemate.util;

import javafx.scene.image.Image;
import javafx.stage.Stage;

public class AppIconLoader {

    public static void applyAppIcons(Stage stage) {
        stage.getIcons().setAll(
                new Image(AppIconLoader.class.getResourceAsStream("/org/example/savemate/img/logo_img_32x32.png")),
                new Image(AppIconLoader.class.getResourceAsStream("/org/example/savemate/img/logo_img_64x64.png")),
                new Image(AppIconLoader.class.getResourceAsStream("/org/example/savemate/img/logo_img_128x128.png")),
                new Image(AppIconLoader.class.getResourceAsStream("/org/example/savemate/img/logo_img_256x256.png")),
                new Image(AppIconLoader.class.getResourceAsStream("/org/example/savemate/img/logo_img_512x512.png"))
        );
    }
}