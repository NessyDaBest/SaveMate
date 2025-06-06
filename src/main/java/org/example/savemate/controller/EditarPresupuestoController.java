package org.example.savemate.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.savemate.database.PresupuestoDAO;
import org.example.savemate.model.Presupuesto;
import org.example.savemate.util.SceneChanger;

public class EditarPresupuestoController {

    @FXML private TextField campoMonto;

    private Presupuesto presupuestoActual;

    public void initData(Presupuesto presupuesto) {
        this.presupuestoActual = presupuesto;
        campoMonto.setText(String.valueOf(presupuesto.getMontoEstimado()));
    }

    @FXML
    private void handleCancelar() {
        ((Stage) campoMonto.getScene().getWindow()).close();
    }

    @FXML
    private void handleGuardar() {
        String montoTexto = campoMonto.getText().trim();

        if (!montoTexto.matches("^[0-9]+([.,][0-9]{1,2})?$")) {
            mostrarAlerta("El monto debe ser un número válido con hasta 2 decimales.");
            return;
        }

        double nuevoMonto = Double.parseDouble(montoTexto.replace(",", "."));
        boolean actualizado = PresupuestoDAO.actualizarMontoPresupuesto(presupuestoActual.getIdPresupuesto(), nuevoMonto);

        Stage modal = (Stage) campoMonto.getScene().getWindow();
        Stage principal = (Stage) modal.getOwner();

        modal.close();

        if (actualizado) {
            Platform.runLater(() -> {
                Alert a = new Alert(Alert.AlertType.INFORMATION, "Presupuesto actualizado correctamente.");
                a.initOwner(principal);
                a.setHeaderText("Información");
                a.showAndWait();
                SceneChanger.changeScene(principal, "/org/example/savemate/fxml/Presupuesto.fxml", "Presupuestos");
            });
        } else {
            Platform.runLater(() -> {
                Alert a = new Alert(Alert.AlertType.ERROR, "Error al actualizar el presupuesto.");
                a.initOwner(principal);
                a.setHeaderText("Error");
                a.showAndWait();
            });
        }
    }

    private void mostrarAlerta(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText("Validación");
        alert.setContentText(msg);
        alert.showAndWait();
    }
}