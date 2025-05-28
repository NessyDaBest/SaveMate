package org.example.savemate.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.example.savemate.util.SceneChanger;
import org.example.savemate.util.UserSession;

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
        Stage mainStage = (Stage) resumenLabel.getScene().getWindow();

        SceneChanger.openFXMLPopup("/org/example/savemate/fxml/UserInfo.fxml", "Cuenta Iniciada",
                (controller, stage) -> {
                    UserInfoController userInfo = (UserInfoController) controller;
                    userInfo.initData(stage, mainStage, UserSession.getNombre(), UserSession.getEmail());
                }
        );
    }

    private void showAlert(String msg, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
