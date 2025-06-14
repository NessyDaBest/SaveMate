package org.example.savemate.controller;

import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.transitions.hamburger.HamburgerSlideCloseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.example.savemate.database.CuentaDAO;
import org.example.savemate.database.GastoDAO;
import org.example.savemate.model.Cuenta;
import org.example.savemate.model.Gasto;
import org.example.savemate.util.SceneChanger;
import org.example.savemate.util.Sesion;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class ListadoGastosController {

    @FXML private TableView<Gasto> tablaGastos;
    @FXML private TableColumn<Gasto, LocalDate> colFecha;
    @FXML private TableColumn<Gasto, String> colDescripcion;
    @FXML private TableColumn<Gasto, Double> colMonto;

    @FXML private Label tituloCuenta;
    @FXML private Button userButton;
    @FXML private JFXDrawer drawer;
    @FXML private JFXHamburger hamburger;
    @FXML private HBox crudButtonBox;

    private Cuenta cuentaActual;
    private HamburgerSlideCloseTransition transition;

    @FXML
    public void initialize() {
        initDrawerContent();
        configurarHamburger();

        ImageView icon = new ImageView(new Image(getClass().getResourceAsStream("/org/example/savemate/img/cuenta_24x24.png")));
        icon.setFitWidth(20);
        icon.setFitHeight(20);
        userButton.setGraphic(icon);

        cuentaActual = Sesion.getCuentaActual();
        if (cuentaActual == null) {
            // No hay cuenta -> modo sin cuenta
            tituloCuenta.setText("Cuenta no encontrada");
            tablaGastos.setDisable(true);
            crudButtonBox.setDisable(true);

            // Mostrar alerta cuando termine la construcción de la UI
            Platform.runLater(() -> mostrarAlerta("No tienes ninguna cuenta creada. Ve a la sección Cuentas para crear una."));

            return;  // ¡OJO! Importantísimo: return para no seguir cargando nada
        }

        tituloCuenta.setText(cuentaActual.getNombre());

        //titulo de cuenta clicable
        tituloCuenta.setOnMouseClicked(e -> {
            SceneChanger.changeScene(
                    (Stage) tituloCuenta.getScene().getWindow(),
                    "/org/example/savemate/fxml/Cuentas.fxml",
                    "Cuentas bancarias"
            );
        });

        colFecha.setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getFecha()));
        colDescripcion.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getDescripcion()));
        colMonto.setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getMonto()));

        List<Gasto> gastos = CuentaDAO.listarGastosPorCuenta(cuentaActual.getIdCuenta());
        tablaGastos.getItems().setAll(gastos);

        hamburger.toFront();
        hamburger.setViewOrder(-1.0);
        userButton.toFront();

        crearBotonesCrud();
    }

    private void configurarHamburger() {
        transition = new HamburgerSlideCloseTransition(hamburger);
        transition.setRate(-1);

        hamburger.setOnMouseClicked(evt -> {
            transition.setRate(transition.getRate() * -1);
            transition.play();
            if (drawer.isOpened()) drawer.close();
            else drawer.open();
        });

        drawer.setOnDrawerOpened(e -> {
            drawer.setMouseTransparent(false);
            hamburger.toFront();
        });

        drawer.setOnDrawerClosed(e -> {
            drawer.setMouseTransparent(true);

            // Reinicia la animación del icono del hamburger a su estado original (☰)
            if (transition.getRate() > 0) {
                transition.setRate(-1); // Vuelve a forma inicial si estaba en forma de 'X'
                transition.play();
            }

            Platform.runLater(() -> {
                drawer.requestLayout();
                drawer.applyCss();
            });
        });

        drawer.setOverLayVisible(true);
    }

    private void initDrawerContent() {
        VBox menu = new VBox(10);
        menu.setStyle("-fx-padding: 10; -fx-background-color: white;");

        String[] opciones = {"Inicio", "Gastos", "Ingresos", "Cuentas", "Limites"};

        for (String txt : opciones) {
            Button btn = new Button(txt);
            MainController.styleMenuButton(btn);

            btn.setOnAction(e -> {
                Stage ventanaActual = (Stage) tituloCuenta.getScene().getWindow();
                switch (txt) {
                    case "Inicio" -> SceneChanger.changeScene(ventanaActual,
                            "/org/example/savemate/fxml/Main.fxml",
                            "SaveMate - Principal");

                    case "Gastos" -> SceneChanger.changeScene(ventanaActual,
                            "/org/example/savemate/fxml/ListadoGastos.fxml",
                            "Listado de Gastos");

                    case "Ingresos" -> SceneChanger.changeScene(ventanaActual,
                            "/org/example/savemate/fxml/ListadoIngresos.fxml",
                            "Listado de Ingresos");

                    case "Cuentas" -> SceneChanger.changeScene(
                            ventanaActual,
                            "/org/example/savemate/fxml/Cuentas.fxml",
                            "Cuentas bancarias"
                    );

                    case "Limites" -> SceneChanger.changeScene(
                            ventanaActual,
                            "/org/example/savemate/fxml/Presupuesto.fxml",
                            "Límites bancarios"
                    );

                    default -> System.out.println("Acción no implementada aún: " + txt);
                }
            });

            menu.getChildren().add(btn);
        }

        drawer.setSidePane(menu);
    }

    public void postInitialize(Stage stage) {
        stage.setMinWidth(720);
        stage.setMinHeight(480);

        drawer.setMouseTransparent(true);
        hamburger.toFront();
        hamburger.setViewOrder(-1.0);
        userButton.toFront();
    }

    private void importarGastosDesdeCSV() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Selecciona un archivo CSV");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));

        Stage stage = (Stage) tablaGastos.getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String linea;
                int importados = 0;

                while ((linea = reader.readLine()) != null) {
                    String[] campos = linea.split(",");

                    if (campos.length >= 3) {
                        try {
                            double monto = Double.parseDouble(campos[0].trim().replace(",", "."));
                            LocalDate fecha = LocalDate.parse(campos[1].trim());
                            String descripcion = campos[2].trim();

                            Gasto gasto = new Gasto(0, monto, fecha, descripcion, cuentaActual.getIdCuenta());
                            GastoDAO.insertarGasto(gasto);
                            importados++;
                        } catch (Exception ex) {
                            System.err.println("Error en línea: " + linea + " → " + ex.getMessage());
                        }
                    }
                }

                recargarTabla();
                mostrarAlerta("Se han importado " + importados + " gastos correctamente.");
            } catch (IOException e) {
                mostrarAlerta("Error al leer el archivo CSV.");
                e.printStackTrace();
            }
        }
    }

    private void crearBotonesCrud() {
        crudButtonBox.getChildren().clear();

        String[] iconos = {
                "/org/example/savemate/img/boton_editar_24x24.png",
                "/org/example/savemate/img/anadir_24x24.png",
                "/org/example/savemate/img/tacho_de_reciclaje_24x24.png"
        };

        String[] tooltips = {
                "Editar",
                "Añadir",
                "Eliminar"
        };

        // Añadir botón de importación CSV
        Button btnImportar = new Button();
        btnImportar.getStyleClass().add("user-button");

        ImageView iconImport = new ImageView(new Image(getClass().getResourceAsStream(
                "/org/example/savemate/img/importar_24x24.png")));
        iconImport.setFitWidth(20);
        iconImport.setFitHeight(20);

        btnImportar.setGraphic(iconImport);
        Tooltip.install(btnImportar, new Tooltip("Importar CSV"));
        btnImportar.setOnAction(e -> importarGastosDesdeCSV());

        crudButtonBox.getChildren().add(btnImportar);

        for (int i = 0; i < iconos.length; i++) {
            Button btn = new Button();
            btn.getStyleClass().add("user-button");  // Reutilizo el estilo visual

            ImageView img = new ImageView(new Image(getClass().getResourceAsStream(iconos[i])));
            img.setFitWidth(20);
            img.setFitHeight(20);

            btn.setGraphic(img);
            Tooltip.install(btn, new Tooltip(tooltips[i]));

            final int index = i;
            btn.setOnAction(e -> {
                Gasto seleccionado = tablaGastos.getSelectionModel().getSelectedItem();

                switch (index) {
                    case 0: // Editar
                        if (seleccionado == null) {
                            mostrarAlerta("Selecciona un gasto para editar.");
                            return;
                        }
                        SceneChanger.openFXMLPopup("/org/example/savemate/fxml/GastoForm.fxml", "Editar Gasto", (controller, stage) -> {
                            ((GastoFormController) controller).initData(stage, cuentaActual.getIdCuenta(), seleccionado);
                            Stage ventanaPrincipal = (Stage) tablaGastos.getScene().getWindow();
                            stage.initOwner(ventanaPrincipal);
                            stage.setOnHiding(ev -> recargarTabla());
                        });
                        break;

                    case 1: // Añadir
                        SceneChanger.openFXMLPopup("/org/example/savemate/fxml/GastoForm.fxml", "Nuevo Gasto", (controller, stage) -> {
                            ((GastoFormController) controller).initData(stage, cuentaActual.getIdCuenta(), null);
                            Stage ventanaPrincipal = (Stage) tablaGastos.getScene().getWindow();
                            stage.initOwner(ventanaPrincipal);
                            stage.setOnHiding(ev -> recargarTabla());
                        });
                        break;

                    case 2: // Eliminar
                        if (seleccionado == null) {
                            mostrarAlerta("Selecciona un gasto para eliminar.");
                            return;
                        }
                        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "¿Eliminar gasto seleccionado?", ButtonType.YES, ButtonType.NO);
                        confirm.showAndWait().ifPresent(resp -> {
                            if (resp == ButtonType.YES) {
                                GastoDAO.eliminarGasto(seleccionado.getIdGasto());
                                recargarTabla();
                            }
                        });
                        break;
                }
            });

            crudButtonBox.getChildren().add(btn);
        }
    }

    @FXML
    private void handleUserIcon() {
        Stage mainStage = (Stage) tituloCuenta.getScene().getWindow();
        String nombre = Sesion.getUsuarioActual().getNombre();
        String email = Sesion.getUsuarioActual().getEmail();

        SceneChanger.openFXMLPopup("/org/example/savemate/fxml/UserInfo.fxml", "Cuenta Iniciada",
                (controller, stage) -> {
                    UserInfoController userInfo = (UserInfoController) controller;
                    userInfo.initData(stage, mainStage, nombre, email);

                    // Hacer que el popup sea modal y bloquee la interacción con la ventana principal
                    stage.initOwner(mainStage);
                    stage.setResizable(false);
                    stage.initModality(javafx.stage.Modality.WINDOW_MODAL);
                    stage.setAlwaysOnTop(true); // opcional, para que siempre esté al frente
                });
    }

    private void mostrarAlerta(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING, msg, ButtonType.OK);
        alert.showAndWait();
    }
    private void recargarTabla() {
        List<Gasto> gastos = CuentaDAO.listarGastosPorCuenta(cuentaActual.getIdCuenta());
        tablaGastos.getItems().setAll(gastos);
    }
}