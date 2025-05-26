package org.example.savemate.util;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class AnimatedButtonFactory {

    public static StackPane create(String text, double width, double height) {
        Button button = new Button(text);
        button.getStyleClass().add("animated-button");
        button.setPrefWidth(width);
        button.setPrefHeight(height);

        // Color inicial del fondo (pastel claro), luego al hacer hover se anima con color más fuerte
        Rectangle effect = new Rectangle(width, height);
        effect.setFill(Color.web("#a8d5ba")); // Color base
        effect.setArcWidth(16);
        effect.setArcHeight(16);

        StackPane container = new StackPane(effect, button);
        container.setPrefSize(width, height);
        container.setAlignment(Pos.CENTER_LEFT); // para el rectángulo animado

        // Segundo rectángulo que se anima encima del color base
        Rectangle animatedLayer = new Rectangle(0, height);
        animatedLayer.setFill(Color.web("#5cbf88")); // Color al hacer hover
        animatedLayer.setArcWidth(16);
        animatedLayer.setArcHeight(16);

        container.getChildren().add(1, animatedLayer); // insertamos en medio: fondo → animación → texto

        // Animación de entrada
        button.setOnMouseEntered(e -> {
            Timeline enter = new Timeline(
                    new KeyFrame(Duration.ZERO, new KeyValue(animatedLayer.widthProperty(), 0)),
                    new KeyFrame(Duration.seconds(0.4), new KeyValue(animatedLayer.widthProperty(), width))
            );
            enter.play();
        });

        // Animación de salida
        button.setOnMouseExited(e -> {
            Timeline exit = new Timeline(
                    new KeyFrame(Duration.ZERO, new KeyValue(animatedLayer.widthProperty(), animatedLayer.getWidth())),
                    new KeyFrame(Duration.seconds(0.3), new KeyValue(animatedLayer.widthProperty(), 0))
            );
            exit.play();
        });

        return container;
    }
}