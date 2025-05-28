package org.example.savemate.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.event.ActionEvent;
import org.example.savemate.util.*;
import org.example.savemate.database.DatabaseConnector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private CheckBox rememberCheckBox;
    @FXML private StackPane animatedButtonContainer;

    @FXML
    public void initialize() {
        // Botón animado
        StackPane animatedLoginButton = AnimatedButtonFactory.create("Iniciar sesión", 250, 40);
        Button innerButton = (Button) animatedLoginButton.getChildren().get(2);
        innerButton.setOnAction(e -> handleLogin(new ActionEvent(innerButton, null)));
        animatedButtonContainer.getChildren().add(animatedLoginButton);

        // Cargar credenciales si "Recuérdame" estaba activado
        String[] saved = RememberMe.loadCredentials();
        if (!saved[0].isEmpty() && !saved[1].isEmpty()) {
            emailField.setText(saved[0]);
            passwordField.setText(saved[1]);
            rememberCheckBox.setSelected(true);
        }
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        String email = emailField.getText();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            showAlert("Por favor, completa todos los campos", Alert.AlertType.WARNING);
            return;
        }

        try (Connection conn = DatabaseConnector.connect()) {
            String query = "SELECT * FROM usuario WHERE email = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String hash = rs.getString("contraseña_hash");

                if (PasswordHasher.check(password, hash)) {

                    // Guardar usuario logeado en sesión
                    UserSession.iniciarSesion(rs.getString("nombre"), email);

                    // Guardar si se marcó "Recuérdame"
                    if (rememberCheckBox.isSelected()) {
                        RememberMe.saveCredentials(email, password);
                    } else {
                        RememberMe.saveCredentials("", "");
                    }

                    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    SceneChanger.changeScene(stage, "/org/example/savemate/fxml/Main.fxml", "SaveMate - Principal");
                } else {
                    CustomAlert.showError("Contraseña incorrecta");
                }
            } else {
                CustomAlert.showError("El correo no está registrado");
            }

        } catch (Exception e) {
            e.printStackTrace();
            CustomAlert.showError("Error al conectar con la base de datos");
        }
    }

    @FXML
    private void goToRegister(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        SceneChanger.changeScene(stage, "/org/example/savemate/fxml/Register.fxml", "Registro");
    }

    private void showAlert(String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setContentText(message);
        alert.showAndWait();
    }
}