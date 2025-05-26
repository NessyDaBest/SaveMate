package org.example.savemate.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;

public class MainController {

    @FXML private Label resumenLabel;

    @FXML
    private void añadirGasto() {
        showAlert("Añadir gasto (simulado)", Alert.AlertType.INFORMATION);
    }

    @FXML
    private void añadirIngreso() {
        showAlert("Añadir ingreso (simulado)", Alert.AlertType.INFORMATION);
    }

    @FXML
    private void verHistorial() {
        showAlert("Historial (simulado)", Alert.AlertType.INFORMATION);
    }

    @FXML
    private void verPresupuestos() {
        showAlert("Presupuestos (simulado)", Alert.AlertType.INFORMATION);
    }

    @FXML
    private void handleUserIcon() {
        showAlert("Opciones de usuario (simulado)", Alert.AlertType.INFORMATION);
    }

    private void showAlert(String msg, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
