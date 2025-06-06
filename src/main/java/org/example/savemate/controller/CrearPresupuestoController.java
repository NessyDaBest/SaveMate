package org.example.savemate.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.savemate.database.PresupuestoDAO;
import org.example.savemate.model.Presupuesto;
import org.example.savemate.util.Sesion;

import java.time.Year;
import java.util.stream.IntStream;

public class CrearPresupuestoController {

    @FXML private TextField campoMonto;
    @FXML private ComboBox<String> comboMes;
    @FXML private ComboBox<Integer> comboAnio;

    private Runnable onPresupuestoCreado;
    public void setOnPresupuestoCreado(Runnable callback) {
        this.onPresupuestoCreado = callback;
    }

    private static final String[] MESES = {
            "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
            "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
    };

    @FXML
    public void initialize() {
        comboMes.getItems().addAll(MESES);
        int añoActual = Year.now().getValue();
        IntStream.rangeClosed(2022, añoActual).forEach(comboAnio.getItems()::add);
    }

    @FXML
    private void handleCancelar() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "¿Seguro que deseas cancelar la creación del presupuesto?",
                ButtonType.YES, ButtonType.NO);
        confirm.setHeaderText("Cancelar");

        Stage stage = (Stage) campoMonto.getScene().getWindow();
        confirm.initOwner(stage);  // Asegura que esté por delante

        confirm.showAndWait().ifPresent(resp -> {
            if (resp == ButtonType.YES) {
                stage.close();
            }
        });
    }

    @FXML
    private void handleCrear() {
        String montoTexto = campoMonto.getText().trim();
        Integer anio = comboAnio.getValue();
        int mes = comboMes.getSelectionModel().getSelectedIndex() + 1;
        int idCuenta = Sesion.getCuentaActual().getIdCuenta();

        if (!montoTexto.matches("^[0-9]+([.,][0-9]{1,2})?$")) {
            mostrarAlerta("El monto debe ser un número válido (hasta 2 decimales).");
            return;
        }
        if (mes < 1 || anio == null) {
            mostrarAlerta("Debes seleccionar un mes y un año.");
            return;
        }

        if (PresupuestoDAO.presupuestoExiste(idCuenta, mes, anio)) {
            mostrarAlerta("Ya existe un presupuesto para ese mes y año en esta cuenta.");
            return;
        }

        double monto = Double.parseDouble(montoTexto.replace(",", "."));
        Presupuesto p = new Presupuesto(monto, mes, anio, idCuenta);
        boolean creado = PresupuestoDAO.crearPresupuesto(p);

        if (creado) {
            if (onPresupuestoCreado != null) onPresupuestoCreado.run();

            // Mostrar alerta antes de cerrar
            Stage stage = (Stage) campoMonto.getScene().getWindow();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("Información");
            alert.setContentText("Presupuesto creado con éxito.");
            alert.initOwner(stage);  // Esto asegura que aparezca encima

            alert.showAndWait();

            // Ahora sí cerramos
            stage.close();
        } else {
            mostrarAlerta("Error al crear el presupuesto.");
        }
    }

    private void mostrarAlerta(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText("Validación");
        alert.setContentText(msg);

        Stage stage = (Stage) campoMonto.getScene().getWindow();  // <- Esto es clave
        alert.initOwner(stage);

        alert.showAndWait();
    }
}