package org.example.savemate.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.example.savemate.util.AnimatedButtonFactory;
import org.example.savemate.util.SceneChanger;
import org.example.savemate.util.UserSession;

public class UserInfoController {

    @FXML private Label nameLabel;
    @FXML private Label emailLabel;
    @FXML private ImageView userIcon;
    @FXML private StackPane logoutButtonContainer;

    private Stage thisStage;
    private Stage mainStage;

    public void initData(Stage thisStage, Stage mainStage, String nombre, String email) {
        this.thisStage = thisStage;
        this.mainStage = mainStage;
        nameLabel.setText(nombre);
        emailLabel.setText(email);
    }

    @FXML
    public void initialize() {
        StackPane animatedLogoutButton = AnimatedButtonFactory.create("Cerrar Sesión", 250, 40);

        // Estilo del texto blanco
        Button innerButton = (Button) animatedLogoutButton.getChildren().get(2);
        innerButton.setStyle("-fx-text-fill: white;");

        // Fondo rojo animado
        animatedLogoutButton.getChildren().get(1).setStyle("-fx-fill: #e53935;");

        innerButton.setOnAction(e -> {
            UserSession.cerrarSesion();
            thisStage.close();
            mainStage.close();
            SceneChanger.openScene("/org/example/savemate/fxml/Login.fxml","Iniciar sesión");
        });

        logoutButtonContainer.getChildren().add(animatedLogoutButton);
    }
}