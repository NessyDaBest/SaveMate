package org.example.savemate.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.event.ActionEvent;
import org.example.savemate.database.DatabaseConnector;
import org.example.savemate.util.CustomAlert;
import org.example.savemate.util.PasswordHasher;
import org.example.savemate.util.SceneChanger;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import org.example.savemate.util.AnimatedButtonFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Tooltip;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import java.sql.Connection;

public class RegisterController {

    @FXML private TextField nombreField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private ImageView emailIcon;
    @FXML private ImageView passwordIcon;
    @FXML private ImageView confirmIcon;
    @FXML private PasswordField confirmPasswordField;
    @FXML private StackPane animatedRegisterButtonContainer;
    @FXML
    public void initialize() {
        StackPane animatedRegisterButton = AnimatedButtonFactory.create("Registrarse", 250, 40);
        Button innerButton = (Button) animatedRegisterButton.getChildren().get(2);
        innerButton.setOnAction(e -> handleRegister(new ActionEvent(innerButton, null)));
        animatedRegisterButtonContainer.getChildren().add(animatedRegisterButton);

        // Listeners de validación
        setupValidation();

        //Detectar Enter en cualquier campo
        nombreField.setOnKeyPressed(event -> {
            if (event.getCode().toString().equals("ENTER")) {
                innerButton.fire();
            }
        });
        emailField.setOnKeyPressed(event -> {
            if (event.getCode().toString().equals("ENTER")) {
                innerButton.fire();
            }
        });
        passwordField.setOnKeyPressed(event -> {
            if (event.getCode().toString().equals("ENTER")) {
                innerButton.fire();
            }
        });
        confirmPasswordField.setOnKeyPressed(event -> {
            if (event.getCode().toString().equals("ENTER")) {
                innerButton.fire();
            }
        });

    }
    private void setupValidation() {
        emailField.textProperty().addListener((obs, oldVal, newVal) -> validateEmail(newVal));
        passwordField.textProperty().addListener((obs, oldVal, newVal) -> validatePassword(newVal));
        confirmPasswordField.textProperty().addListener((obs, oldVal, newVal) -> validateConfirmPassword(newVal));
    }
    private void validateEmail(String input) {
        String regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        boolean valid = input.matches(regex);
        updateFieldVisual(emailField, emailIcon, valid, "Correo no válido");
    }
    private void validatePassword(String input) {
        String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$";
        boolean valid = input.matches(regex);
        updateFieldVisual(passwordField, passwordIcon, valid, "La contraseña debe tener al menos 8 caracteres, una minúscula, una mayúscula y un número.");
    }
    private void validateConfirmPassword(String input) {
        boolean valid = input.equals(passwordField.getText());
        updateFieldVisual(confirmPasswordField, confirmIcon, valid, "Las contraseñas no coinciden.");
    }
    private void updateFieldVisual(TextField field, ImageView icon, boolean valid, String tooltipText) {
        String greenBorder = "-fx-border-color: #4CAF50; -fx-border-radius: 4;";
        String redBorder = "-fx-border-color: #e53935; -fx-border-radius: 4;";

        icon.setImage(new Image(getClass().getResourceAsStream(
                valid ? "/org/example/savemate/img/verify_16x16.png" : "/org/example/savemate/img/incorrect_16x16.png"
        )));
        icon.setVisible(true);
        field.setStyle(valid ? greenBorder : redBorder);

        if (!valid) {
            Tooltip tooltip = new Tooltip(tooltipText);
            Tooltip.install(field, tooltip);
        } else {
            Tooltip.uninstall(field, null);
        }
    }

    @FXML
    private void handleRegister(ActionEvent event) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$";
        String nombre = nombreField.getText();
        String email = emailField.getText();
        String pass1 = passwordField.getText();
        String pass2 = confirmPasswordField.getText();

        if (!email.matches(emailRegex)) {
            CustomAlert.showError("Correo inválido. Introduce uno correcto.");
            return;
        }

        if (!pass1.matches(passwordRegex)) {
            CustomAlert.showError("Contraseña no válida. Debe contener al menos 8 caracteres, una mayúscula, una minúscula y un número.");
            return;
        }

        if (nombre.isEmpty() || email.isEmpty() || pass1.isEmpty() || pass2.isEmpty()) {
            CustomAlert.showError("Todos los campos son obligatorios");
            return;
        }

        if (!pass1.equals(pass2)) {
            CustomAlert.showError("Las contraseñas no coinciden");
            return;
        }

        String hash = PasswordHasher.hash(pass1);

        try (Connection conn = DatabaseConnector.connect()) {
            var ps = conn.prepareStatement("INSERT INTO usuario (nombre, email, contraseña_hash) VALUES (?, ?, ?)");
            ps.setString(1, nombre);
            ps.setString(2, email);
            ps.setString(3, hash);
            ps.executeUpdate();

            CustomAlert.showInfo("Usuario registrado correctamente");
            goToLogin(event);
        } catch (Exception e) {
            CustomAlert.showError("Error al registrar usuario: " + e.getMessage());
        }
    }

    @FXML
    private void goToLogin(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        SceneChanger.changeScene(stage, "/org/example/savemate/fxml/Login.fxml", "Iniciar sesión");
    }
}
