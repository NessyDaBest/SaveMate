package org.example.savemate.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.savemate.database.CuentaDAO;
import org.example.savemate.model.Cuenta;
import org.example.savemate.util.SceneChanger;
import org.example.savemate.util.Sesion;

public class CrearCuentaController {

    @FXML private TextField campoNombre;
    @FXML private TextField campoSaldo;
    @FXML private TextField campoBanco;

    @FXML
    private void handleCancelar() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "¿Seguro que deseas cancelar la creación de la cuenta?", ButtonType.YES, ButtonType.NO);
        confirm.setHeaderText("Confirmar cancelación");
        confirm.showAndWait().ifPresent(resp -> {
            if (resp == ButtonType.YES) {
                Stage stage = (Stage) campoNombre.getScene().getWindow();
                stage.close();
            }
        });
    }

    @FXML
    private void handleCrear() {
        String nombre = campoNombre.getText().trim();
        String saldoTexto = campoSaldo.getText().trim();
        String banco = campoBanco.getText().trim();

        if (nombre.isEmpty() || !nombre.matches("[a-zA-ZÁÉÍÓÚáéíóúñÑ ]+")) {
            mostrarAlerta("El nombre solo puede contener letras y espacios.");
            return;
        }

        if (banco.isEmpty() || !banco.matches("[a-zA-ZÁÉÍÓÚáéíóúñÑ ]+")) {
            mostrarAlerta("El banco solo puede contener letras y espacios.");
            return;
        }

        if (!saldoTexto.matches("^[0-9]+([.,][0-9]{1,2})?$")) {
            mostrarAlerta("El saldo debe ser un número válido con hasta 2 decimales.");
            return;
        }

        double saldo = Double.parseDouble(saldoTexto.replace(",", "."));
        int idUsuario = Sesion.getUsuarioActual().getIdUsuario();

        boolean exito = CuentaDAO.crearCuenta(idUsuario, nombre, banco, saldo);
        if (exito) {
            Cuenta nueva = CuentaDAO.obtenerCuentaReciente(idUsuario);
            if (nueva != null) {
                Sesion.setCuentaActual(nueva);  // por si quieres usarla más adelante
            }
            ((Stage) campoNombre.getScene().getWindow()).close();
        } else {
            mostrarAlerta("Error al crear la cuenta.");
        }
    }

    private void mostrarAlerta(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText("Validación");
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void mostrarInfo(String msg, Runnable callback) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("Información");
        alert.setContentText(msg);
        alert.initOwner(campoNombre.getScene().getWindow());
        alert.showAndWait().ifPresent(resp -> {
            if (resp == ButtonType.OK && callback != null) callback.run();
        });
    }
}