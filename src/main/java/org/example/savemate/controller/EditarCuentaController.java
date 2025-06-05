package org.example.savemate.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.savemate.database.CuentaDAO;
import org.example.savemate.model.Cuenta;
import org.example.savemate.util.SceneChanger;

public class EditarCuentaController {

    @FXML private TextField campoNombre;

    private Cuenta cuentaActual;

    public void initData(Cuenta cuenta) {
        this.cuentaActual = cuenta;
        campoNombre.setText(cuenta.getNombre());
    }

    @FXML
    private void handleCancelar() {
        ((Stage) campoNombre.getScene().getWindow()).close();
    }

    @FXML
    private void handleGuardar() {
        String nuevoNombre = campoNombre.getText().trim();
        if (nuevoNombre.isEmpty() || !nuevoNombre.matches("[a-zA-ZÁÉÍÓÚáéíóúñÑ ]+")) {
            Alert a = new Alert(Alert.AlertType.WARNING, "El nombre solo puede contener letras y espacios.");
            a.showAndWait();
            return;
        }

        boolean actualizado = CuentaDAO.actualizarNombreCuenta(cuentaActual.getIdCuenta(), nuevoNombre);
        Stage modal = (Stage) campoNombre.getScene().getWindow();
        Stage principal = (Stage) modal.getOwner(); // muy importante

        modal.close();

        if (actualizado) {
            Platform.runLater(() -> {
                Alert a = new Alert(Alert.AlertType.INFORMATION, "Cuenta actualizada correctamente.");
                a.initOwner(principal);
                a.setHeaderText("Información");
                a.showAndWait();
                SceneChanger.changeScene(principal, "/org/example/savemate/fxml/Cuentas.fxml", "Cuentas bancarias");
            });
        } else {
            Platform.runLater(() -> {
                Alert a = new Alert(Alert.AlertType.ERROR, "Error al actualizar la cuenta.");
                a.initOwner(principal);
                a.setHeaderText("Error");
                a.showAndWait();
            });
        }
    }
}