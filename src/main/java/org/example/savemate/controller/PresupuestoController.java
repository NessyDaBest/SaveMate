package org.example.savemate.controller;

import com.jfoenix.controls.*;
import com.jfoenix.transitions.hamburger.HamburgerSlideCloseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.example.savemate.database.PresupuestoDAO;
import org.example.savemate.model.Cuenta;
import org.example.savemate.model.Presupuesto;
import org.example.savemate.util.SceneChanger;
import org.example.savemate.util.Sesion;

import java.time.Month;
import java.util.List;

public class PresupuestoController {

    @FXML private Label tituloCuenta;
    @FXML private Button userButton;
    @FXML private JFXDrawer drawer;
    @FXML private JFXHamburger hamburger;
    @FXML private VBox contenedorPresupuestos;
    @FXML private StackPane botonNuevoPresupuesto;
    private Cuenta cuentaActual;

    private HamburgerSlideCloseTransition transition;

    @FXML
    public void initialize() {
        configurarHamburger();
        initDrawerContent();

        ImageView icon = new ImageView(new Image(getClass().getResourceAsStream("/org/example/savemate/img/cuenta_24x24.png")));
        icon.setFitWidth(20);
        icon.setFitHeight(20);
        userButton.setGraphic(icon);

        botonNuevoPresupuesto.getStyleClass().add("boton-anadir-cuenta");
        ImageView iconoMas = new ImageView(new Image(getClass().getResourceAsStream("/org/example/savemate/img/anadir_32x32.png")));
        iconoMas.setFitWidth(28);
        iconoMas.setFitHeight(28);
        botonNuevoPresupuesto.getChildren().add(iconoMas);

        botonNuevoPresupuesto.setOnMouseClicked(e -> {
            Stage mainStage = (Stage) botonNuevoPresupuesto.getScene().getWindow();
            final boolean[] presupuestoCreado = {false};

            SceneChanger.openFXMLPopup("/org/example/savemate/fxml/CrearPresupuesto.fxml", "Nuevo Presupuesto", (controller, modalStage) -> {
                CrearPresupuestoController c = (CrearPresupuestoController) controller;
                c.setOnPresupuestoCreado(() -> presupuestoCreado[0] = true);

                modalStage.initOwner(mainStage);
                modalStage.setResizable(false);
                modalStage.initModality(javafx.stage.Modality.WINDOW_MODAL);
                modalStage.setAlwaysOnTop(true);

                modalStage.setOnHiding(ev -> {
                    if (presupuestoCreado[0]) {
                        SceneChanger.changeScene(mainStage, "/org/example/savemate/fxml/Main.fxml", "SaveMate - Principal");
                    }
                });
            });
        });

        // === Aquí va el control de cuenta ===
        cuentaActual = Sesion.getCuentaActual();
        if (cuentaActual == null) {
            tituloCuenta.setText("Cuenta no encontrada");
            contenedorPresupuestos.setDisable(true);
            botonNuevoPresupuesto.setDisable(true);
            Platform.runLater(() -> mostrarAlerta("No tienes ninguna cuenta creada. Ve a la sección Cuentas para crear una."));
            return;
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

        cargarPresupuestos();
    }

    private void cargarPresupuestos() {
        contenedorPresupuestos.getChildren().clear();
        int idCuenta = Sesion.getCuentaActual().getIdCuenta();
        List<Presupuesto> presupuestos = PresupuestoDAO.obtenerPresupuestosPorCuenta(idCuenta);

        for (Presupuesto p : presupuestos) {
            HBox fila = new HBox(10);
            fila.getStyleClass().add("cuenta-item");
            fila.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
            fila.setMaxWidth(320);

            Label nombre = new Label("Límite");
            Label mes = new Label(Month.of(p.getMes()).name().substring(0, 1) + Month.of(p.getMes()).name().substring(1).toLowerCase());
            Label año = new Label(String.valueOf(p.getAnio()));

            nombre.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
            mes.setStyle("-fx-font-size: 13px;");
            año.setStyle("-fx-font-size: 13px;");

            HBox.setHgrow(nombre, Priority.ALWAYS);

            Button btnEditar = new Button();
            ImageView iconEditar = new ImageView(new Image(getClass().getResourceAsStream("/org/example/savemate/img/boton_editar_24x24.png")));
            iconEditar.setFitWidth(20);
            iconEditar.setFitHeight(20);
            btnEditar.setGraphic(iconEditar);
            btnEditar.getStyleClass().add("user-button");

            btnEditar.setOnAction(e -> {
                Stage mainStage = (Stage) contenedorPresupuestos.getScene().getWindow();
                SceneChanger.openFXMLPopup("/org/example/savemate/fxml/EditarPresupuesto.fxml", "Editar Presupuesto", (controller, stage) -> {
                    EditarPresupuestoController c = (EditarPresupuestoController) controller;
                    c.initData(p);
                    stage.initOwner(mainStage);
                    stage.setResizable(false);
                    stage.initModality(javafx.stage.Modality.WINDOW_MODAL);
                    stage.setAlwaysOnTop(true);
                });
            });

            Button btnEliminar = new Button();
            ImageView iconEliminar = new ImageView(new Image(getClass().getResourceAsStream("/org/example/savemate/img/tacho_de_reciclaje_24x24.png")));
            iconEliminar.setFitWidth(20);
            iconEliminar.setFitHeight(20);
            btnEliminar.setGraphic(iconEliminar);
            btnEliminar.getStyleClass().add("user-button");

            btnEliminar.setOnAction(e -> {
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "¿Eliminar este presupuesto?", ButtonType.YES, ButtonType.NO);
                confirm.setHeaderText("Confirmar eliminación");
                confirm.initOwner(contenedorPresupuestos.getScene().getWindow());
                confirm.showAndWait().ifPresent(resp -> {
                    if (resp == ButtonType.YES) {
                        PresupuestoDAO.eliminarPresupuesto(p.getIdPresupuesto());
                        SceneChanger.changeScene((Stage) contenedorPresupuestos.getScene().getWindow(), "/org/example/savemate/fxml/Presupuesto.fxml", "Presupuestos");
                    }
                });
            });

            fila.getChildren().addAll(nombre, mes, año, btnEditar, btnEliminar);
            contenedorPresupuestos.getChildren().add(fila);
        }
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
            if (transition.getRate() > 0) {
                transition.setRate(-1);
                transition.play();
            }
            Platform.runLater(() -> {
                drawer.requestLayout();
                drawer.applyCss();
            });
        });

        drawer.setOverLayVisible(true);
    }

    public void postInitialize(Stage stage) {
        stage.setMinWidth(720);
        stage.setMinHeight(480);

        drawer.setMouseTransparent(true);
        hamburger.toFront();
        hamburger.setViewOrder(-1.0);
        userButton.toFront();
    }

    private void initDrawerContent() {
        VBox menu = new VBox(10);
        menu.setStyle("-fx-padding: 10; -fx-background-color: white;");
        String[] opciones = {"Inicio", "Gastos", "Ingresos", "Cuentas", "Limites"};
        for (String txt : opciones) {
            Button btn = new Button(txt);
            MainController.styleMenuButton(btn);
            btn.setOnAction(e -> {
                Stage actual = (Stage) tituloCuenta.getScene().getWindow();
                switch (txt) {
                    case "Inicio" -> SceneChanger.changeScene(actual, "/org/example/savemate/fxml/Main.fxml", "SaveMate - Principal");
                    case "Gastos" -> SceneChanger.changeScene(actual, "/org/example/savemate/fxml/ListadoGastos.fxml", "Listado de Gastos");
                    case "Ingresos" -> SceneChanger.changeScene(actual, "/org/example/savemate/fxml/ListadoIngresos.fxml", "Listado de Ingresos");
                    case "Cuentas" -> SceneChanger.changeScene(actual, "/org/example/savemate/fxml/Cuentas.fxml", "Cuentas bancarias");
                    case "Limites" -> SceneChanger.changeScene(actual, "/org/example/savemate/fxml/Presupuesto.fxml", "Límites bancarios");
                }
            });
            menu.getChildren().add(btn);
        }
        drawer.setSidePane(menu);
    }

    @FXML
    private void handleUserIcon() {
        Stage mainStage = (Stage) tituloCuenta.getScene().getWindow();
        String nombre = Sesion.getUsuarioActual().getNombre();
        String email = Sesion.getUsuarioActual().getEmail();
        SceneChanger.openFXMLPopup("/org/example/savemate/fxml/UserInfo.fxml", "Cuenta Iniciada", (controller, stage) -> {
            UserInfoController c = (UserInfoController) controller;
            c.initData(stage, mainStage, nombre, email);
            stage.initOwner(mainStage);
            stage.setResizable(false);
            stage.initModality(javafx.stage.Modality.WINDOW_MODAL);
            stage.setAlwaysOnTop(true);
        });
    }

    private void mostrarAlerta(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING, msg, ButtonType.OK);
        alert.showAndWait();
    }
}