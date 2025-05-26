package org.example.savemate.util;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class CustomAlert {

    public static void showError(String message) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Error");
        dialog.getIcons().add(new Image(CustomAlert.class.getResourceAsStream(
                "/org/example/savemate/img/incorrect_16x16.png")));

        // Contenedor principal
        HBox content = new HBox(10);
        content.setPadding(new Insets(20));
        content.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));

        // Icono grande
        ImageView icon = new ImageView(new Image(CustomAlert.class.getResourceAsStream(
                "/org/example/savemate/img/incorrect_64x64.png")));
        icon.setFitWidth(64);
        icon.setFitHeight(64);

        // Mensaje
        Label label = new Label(message);
        label.setWrapText(true);
        label.setStyle("-fx-font-size: 14px;");

        // Botón cerrar
        Button closeBtn = new Button("Aceptar");
        closeBtn.setOnAction(e -> dialog.close());
        closeBtn.setDefaultButton(true);

        VBox right = new VBox(10, label, closeBtn);
        right.setPadding(new Insets(0, 0, 0, 10));

        content.getChildren().addAll(icon, right);

        Scene scene = new Scene(content);
        dialog.setScene(scene);
        dialog.setResizable(false);
        dialog.initStyle(StageStyle.DECORATED);
        dialog.showAndWait();
    }
    public static void showInfo(String message) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Información");
        dialog.getIcons().add(new Image(CustomAlert.class.getResourceAsStream(
                "/org/example/savemate/img/verify_16x16.png")));

        // Contenedor principal
        HBox content = new HBox(10);
        content.setPadding(new Insets(20));
        content.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));

        // Icono grande
        ImageView icon = new ImageView(new Image(CustomAlert.class.getResourceAsStream(
                "/org/example/savemate/img/verify_64x64.png")));
        icon.setFitWidth(64);
        icon.setFitHeight(64);

        // Mensaje
        Label label = new Label(message);
        label.setWrapText(true);
        label.setStyle("-fx-font-size: 14px;");

        // Botón cerrar
        Button closeBtn = new Button("Aceptar");
        closeBtn.setOnAction(e -> dialog.close());
        closeBtn.setDefaultButton(true);

        VBox right = new VBox(10, label, closeBtn);
        right.setPadding(new Insets(0, 0, 0, 10));

        content.getChildren().addAll(icon, right);

        Scene scene = new Scene(content);
        dialog.setScene(scene);
        dialog.setResizable(false);
        dialog.initStyle(StageStyle.DECORATED);
        dialog.showAndWait();
    }
}