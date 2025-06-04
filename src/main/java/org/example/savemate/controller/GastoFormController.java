package org.example.savemate.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.savemate.database.GastoDAO;
import org.example.savemate.model.Gasto;

import java.time.LocalDate;

public class GastoFormController {

    @FXML private TextField campoMonto;
    @FXML private DatePicker campoFecha;
    @FXML private TextArea campoDescripcion;
    @FXML private Button btnAceptar;
    @FXML private Button btnCancelar;

    private Stage thisStage;
    private boolean esEdicion = false;
    private Gasto gastoOriginal;
    private int idCuenta;

    public void initData(Stage thisStage, int idCuenta, Gasto gastoEditar) {
        this.thisStage = thisStage;
        this.idCuenta = idCuenta;

        campoFecha.setValue(LocalDate.now());

        if (gastoEditar != null) {
            esEdicion = true;
            gastoOriginal = gastoEditar;
            campoMonto.setText(String.valueOf(gastoEditar.getMonto()));
            campoFecha.setValue(gastoEditar.getFecha());
            campoDescripcion.setText(gastoEditar.getDescripcion());
        }

        btnAceptar.setOnAction(e -> handleGuardar());
        btnCancelar.setOnAction(e -> handleCancelar());
    }

    private void handleGuardar() {
        String descripcion = campoDescripcion.getText().trim();
        String montoStr = campoMonto.getText().replace(",", ".").trim();
        LocalDate fecha = campoFecha.getValue();

        if (descripcion.isEmpty() || montoStr.isEmpty() || fecha == null) {
            mostrarAlerta("Todos los campos deben estar completos.");
            return;
        }

        if (descripcion.length() > 50) {
            mostrarAlerta("La descripción no puede superar los 50 caracteres.");
            return;
        }

        double monto;
        try {
            monto = Double.parseDouble(montoStr);
        } catch (NumberFormatException e) {
            mostrarAlerta("El monto debe ser un número válido con punto o coma.");
            return;
        }

        if (esEdicion) {
            gastoOriginal = new Gasto(gastoOriginal.getIdGasto(), monto, fecha, descripcion, idCuenta);
            GastoDAO.actualizarGasto(gastoOriginal);
        } else {
            Gasto nuevo = new Gasto(0, monto, fecha, descripcion, idCuenta);
            GastoDAO.insertarGasto(nuevo);
        }

        thisStage.close();
    }

    private void handleCancelar() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "¿Cancelar la operación?", ButtonType.YES, ButtonType.NO);
        alert.showAndWait().ifPresent(resp -> {
            if (resp == ButtonType.YES) {
                thisStage.close();
            }
        });
    }

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING, mensaje, ButtonType.OK);
        alert.showAndWait();
    }
}