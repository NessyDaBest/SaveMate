package org.example.savemate.controller;

import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.transitions.hamburger.HamburgerSlideCloseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.savemate.database.CuentaDAO;
import org.example.savemate.model.Cuenta;
import org.example.savemate.model.Ingreso;
import org.example.savemate.util.SceneChanger;
import org.example.savemate.util.Sesion;

import java.time.LocalDate;
import java.util.List;

public class ListadoIngresosController {

    @FXML private TableView<Ingreso> tablaIngresos;
    @FXML private TableColumn<Ingreso, LocalDate> colFecha;
    @FXML private TableColumn<Ingreso, String> colDescripcion;
    @FXML private TableColumn<Ingreso, Double> colMonto;

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

        // Datos usuario y cuenta
        cuentaActual = CuentaDAO.obtenerCuentaPorUsuario(Sesion.getUsuarioActual().getIdUsuario());
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

        List<Ingreso> ingresos = CuentaDAO.listarIngresosPorCuenta(cuentaActual.getIdCuenta());
        tablaIngresos.getItems().setAll(ingresos);

        hamburger.toFront();
        hamburger.setViewOrder(-1.0);
        userButton.toFront();
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

        String[] opciones = {"Inicio", "Añadir Gasto", "Añadir Ingreso", "Crear Cuenta", "Presupuesto"};
        for (String txt : opciones) {
            Button btn = new Button(txt);
            MainController.styleMenuButton(btn); // si usas el estilo común como static
            btn.setOnAction(e -> {
                switch (txt) {
                    case "Inicio" -> SceneChanger.changeScene((Stage) tituloCuenta.getScene().getWindow(), "/org/example/savemate/fxml/Main.fxml", "SaveMate - Principal");
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
}