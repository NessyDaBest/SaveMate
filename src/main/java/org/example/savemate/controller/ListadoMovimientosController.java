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
import javafx.stage.Stage;
import org.example.savemate.database.CuentaDAO;
import org.example.savemate.model.Cuenta;
import org.example.savemate.model.Ingreso;
import org.example.savemate.model.Movimiento;
import org.example.savemate.util.SceneChanger;
import org.example.savemate.util.Sesion;

import java.time.LocalDate;
import java.util.List;

public class ListadoMovimientosController {

    @FXML private TableView<Movimiento> tablaMovimientos;
    @FXML private TableColumn<Movimiento, LocalDate> colFecha;
    @FXML private TableColumn<Movimiento, String> colDescripcion;
    @FXML private TableColumn<Movimiento, Double> colMonto;
    @FXML private HBox crudButtonBox;

    @FXML private Label tituloCuenta;
    @FXML private Button userButton;
    @FXML private JFXDrawer drawer;
    @FXML private JFXHamburger hamburger;

    private Cuenta cuentaActual;
    private HamburgerSlideCloseTransition transition;

    @FXML
    public void initialize() {
        initDrawerContent();
        configurarHamburger();

        cuentaActual = Sesion.getCuentaActual();
        if (cuentaActual == null) {
            // No hay cuenta
            tituloCuenta.setText("Cuenta no encontrada");
            tablaMovimientos.setDisable(true);
            Platform.runLater(() -> mostrarAlerta("No tienes ninguna cuenta creada. Ve a la sección Cuentas para crear una."));
            return;
        }

        //titulo de cuenta clicable
        tituloCuenta.setOnMouseClicked(e -> {
            SceneChanger.changeScene(
                    (Stage) tituloCuenta.getScene().getWindow(),
                    "/org/example/savemate/fxml/Cuentas.fxml",
                    "Cuentas bancarias"
            );
        });

        // Datos usuario y cuenta
        cuentaActual = Sesion.getCuentaActual();
        if (cuentaActual != null) {
            tituloCuenta.setText(cuentaActual.getNombre());
        }

        ImageView icon = new ImageView(new Image(getClass().getResourceAsStream("/org/example/savemate/img/cuenta_24x24.png")));
        icon.setFitWidth(20);
        icon.setFitHeight(20);
        userButton.setGraphic(icon);

        colFecha.setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getFecha()));
        colDescripcion.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getDescripcion()));
        colMonto.setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getMonto()));

        colMonto.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Double monto, boolean empty) {
                super.updateItem(monto, empty);
                if (empty || monto == null) {
                    setText(null);
                    setStyle("");
                } else {
                    Movimiento movimiento = getTableView().getItems().get(getIndex());
                    String signo = movimiento.isEsIngreso() ? "+" : "-";
                    setText(String.format("%s%.2f €",signo, monto));
                    if (movimiento.isEsIngreso()) {
                        setStyle("-fx-text-fill: #75BB8B; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: #C73C31; -fx-font-weight: bold;");
                    }
                }
            }
        });

        // Cargar datos
        List<Movimiento> movimientos = CuentaDAO.listarMovimientosPorCuenta(cuentaActual.getIdCuenta());
        tablaMovimientos.getItems().setAll(movimientos);

        hamburger.toFront();
        hamburger.setViewOrder(-1.0);
        userButton.toFront();

        //crearBotonesCrud();
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

    /*
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

        for (int i = 0; i < iconos.length; i++) {
            Button btn = new Button();
            btn.getStyleClass().add("user-button");  // Reutilizo el estilo visual

            ImageView img = new ImageView(new Image(getClass().getResourceAsStream(iconos[i])));
            img.setFitWidth(20);
            img.setFitHeight(20);

            btn.setGraphic(img);
            Tooltip.install(btn,new Tooltip(tooltips[i]));

            crudButtonBox.getChildren().add(btn);
        }
    }

     */

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
}