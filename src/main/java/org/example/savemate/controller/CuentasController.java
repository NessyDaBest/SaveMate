package org.example.savemate.controller;

import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.controls.JFXRippler;
import com.jfoenix.transitions.hamburger.HamburgerSlideCloseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.example.savemate.database.CuentaDAO;
import org.example.savemate.model.Cuenta;
import org.example.savemate.util.SceneChanger;
import org.example.savemate.util.Sesion;

import java.util.List;

public class CuentasController {

    @FXML private Label tituloCuenta;
    @FXML private Button userButton;
    @FXML private JFXDrawer drawer;
    @FXML private JFXHamburger hamburger;
    @FXML private VBox contenedorCuentas;
    @FXML private StackPane botonNuevaCuenta;

    private HamburgerSlideCloseTransition transition;

    @FXML
    public void initialize() {
        //Icono de añadir
        ImageView iconoMas = new ImageView(new Image(getClass().getResourceAsStream("/org/example/savemate/img/anadir_32x32.png")));
        iconoMas.setFitWidth(28);
        iconoMas.setFitHeight(28);

        // Aplicar estilo al StackPane para que se vea como los demás
        botonNuevaCuenta.getStyleClass().add("boton-anadir-cuenta");

        botonNuevaCuenta.getChildren().add(iconoMas);

        // Nombre de cuenta en la barra superior
        Cuenta actual = CuentaDAO.obtenerCuentaPorUsuario(Sesion.getUsuarioActual().getIdUsuario());
        tituloCuenta.setText(actual != null ? actual.getNombre() : "Mis cuentas");

        configurarHamburger();
        initDrawerContent();

        ImageView icon = new ImageView(new Image(getClass().getResourceAsStream("/org/example/savemate/img/cuenta_24x24.png")));
        icon.setFitWidth(20);
        icon.setFitHeight(20);
        userButton.setGraphic(icon);

        botonNuevaCuenta.setOnMouseClicked(e -> {
            Stage mainStage = (Stage) botonNuevaCuenta.getScene().getWindow();
            SceneChanger.openFXMLPopup("/org/example/savemate/fxml/CrearCuenta.fxml", "Nueva Cuenta", (controller, modalStage) -> {
                modalStage.initOwner(mainStage);
                modalStage.setResizable(false);
                modalStage.initModality(javafx.stage.Modality.WINDOW_MODAL);
                modalStage.setAlwaysOnTop(true);

                // Al cerrar con la X, actualiza directamente la escena
                modalStage.setOnHiding(ev -> {
                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setHeaderText("Información");
                        alert.setContentText("Cuenta creada con éxito.");
                        alert.initOwner(mainStage); // muy importante para que se muestre por delante
                        alert.showAndWait();

                        SceneChanger.changeScene(
                                mainStage,
                                "/org/example/savemate/fxml/Cuentas.fxml",
                                "Cuentas bancarias"
                        );
                    });
                });
            });
        });

        cargarCuentas();
    }

    private void cargarCuentas() {
        contenedorCuentas.getChildren().clear();

        List<Cuenta> cuentas = CuentaDAO.listarCuentasDeUsuario(Sesion.getUsuarioActual().getIdUsuario());

        for (Cuenta cuenta : cuentas) {
            HBox fila = new HBox(10);
            fila.getStyleClass().add("cuenta-item");
            fila.setPrefWidth(300);
            fila.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

            Label nombre = new Label(cuenta.getNombre());
            nombre.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
            HBox.setHgrow(nombre, Priority.ALWAYS);

            ImageView iconEditar = new ImageView(new Image(getClass().getResourceAsStream("/org/example/savemate/img/boton_editar_24x24.png")));
            ImageView iconEliminar = new ImageView(new Image(getClass().getResourceAsStream("/org/example/savemate/img/tacho_de_reciclaje_24x24.png")));
            iconEditar.setFitWidth(20);
            iconEditar.setFitHeight(20);
            iconEliminar.setFitWidth(20);
            iconEliminar.setFitHeight(20);

            Button btnEditar = new Button();
            btnEditar.setGraphic(iconEditar);
            btnEditar.getStyleClass().add("user-button");

            Button btnEliminar = new Button();
            btnEliminar.setGraphic(iconEliminar);
            btnEliminar.getStyleClass().add("user-button");

            fila.getChildren().addAll(nombre, btnEditar, btnEliminar);

            JFXRippler rippler = new JFXRippler(fila);
            rippler.setStyle("-fx-alignment: center;");
            Tooltip.install(rippler, new Tooltip("Hacer clic para seleccionar esta cuenta"));

            // Evento de clic: cambia de cuenta y redirige la ventana de cuentas
            rippler.setOnMouseClicked(e -> {
                Sesion.setCuentaActual(cuenta);
                SceneChanger.changeScene(
                        (Stage) contenedorCuentas.getScene().getWindow(),
                        "/org/example/savemate/fxml/Main.fxml",
                        "SaveMate - Principal"
                );
            });

            contenedorCuentas.getChildren().add(rippler);
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

    private void initDrawerContent() {
        VBox menu = new VBox(10);
        menu.setStyle("-fx-padding: 10; -fx-background-color: white;");

        String[] opciones = {"Inicio", "Gastos", "Ingresos", "Cuentas", "Presupuesto"};
        for (String txt : opciones) {
            Button btn = new Button(txt);
            MainController.styleMenuButton(btn);

            btn.setOnAction(e -> {
                Stage actual = (Stage) tituloCuenta.getScene().getWindow();
                switch (txt) {
                    case "Inicio" -> SceneChanger.changeScene(actual, "/org/example/savemate/fxml/Main.fxml", "SaveMate - Principal");
                    case "Gastos" -> SceneChanger.changeScene(actual, "/org/example/savemate/fxml/ListadoGastos.fxml", "Listado de Gastos");
                    case "Ingresos" -> SceneChanger.changeScene(actual, "/org/example/savemate/fxml/ListadoIngresos.fxml", "Listado de Ingresos");
                    case "Cuentas" -> {} // Ya estamos aquí
                    case "Presupuesto" -> {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Esta sección aún no está implementada.", ButtonType.OK);
                        alert.setHeaderText("En desarrollo");
                        alert.showAndWait();
                    }
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
}
