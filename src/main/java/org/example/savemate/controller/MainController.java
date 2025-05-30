package org.example.savemate.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.transitions.hamburger.HamburgerSlideCloseTransition;

import org.example.savemate.database.CuentaDAO;
import org.example.savemate.model.Cuenta;
import org.example.savemate.util.SceneChanger;
import org.example.savemate.util.Sesion;

import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;

public class MainController {

    @FXML private JFXDrawer drawer;
    @FXML private JFXHamburger hamburger;
    @FXML private Button userButton;
    @FXML private Label tituloCuenta;
    @FXML private Label labelGasto;
    @FXML private Label labelIngreso;
    @FXML private Label labelSaldo;
    @FXML private BarChart<String, Number> gastoMensualChart;

    private HamburgerSlideCloseTransition transition;
    private Cuenta cuentaActual;

    @FXML
    public void initialize() {
        //tituloCuenta.setText("Cuenta Ahorros");
        // Obtener y mostrar cuenta del usuario
        cargarCuentaDelUsuario();

        // Animación del botón hamburguesa
        transition = new HamburgerSlideCloseTransition(hamburger);
        transition.setRate(-1);

        // Icono de usuario
        ImageView iconoUsuario = new ImageView(new Image(
                getClass().getResourceAsStream("/org/example/savemate/img/cuenta_24x24.png")));
        iconoUsuario.setFitWidth(20);
        iconoUsuario.setFitHeight(20);
        userButton.setGraphic(iconoUsuario);

        // Configuración inicial del drawer
        drawer.setMouseTransparent(true);
        hamburger.setViewOrder(-1.0);
        hamburger.toFront();

        // Toggle al click en la hamburguesa
        hamburger.setOnMouseClicked(evt -> {
            transition.setRate(transition.getRate() * -1);
            transition.play();
            if (drawer.isOpened()) {
                drawer.close();
            } else {
                drawer.open();
            }
        });

        // Cuando el drawer se abra
        drawer.setOnDrawerOpened(e -> {
            drawer.setMouseTransparent(false);
            hamburger.setMouseTransparent(false);
            hamburger.toFront();
        });

        // Cuando el drawer se cierre
        drawer.setOnDrawerClosed(e -> {
            drawer.setMouseTransparent(true);
            userButton.setGraphic(iconoUsuario);

            // Reset de la animación si hiciera falta
            if (transition.getRate() > 0) {
                transition.setRate(-1);
                transition.play();
            }

            // Forzar un nuevo pase de layout para que el drawer se reposicione
            Platform.runLater(() -> {
                // Puedes pedir layout del drawer o de su root
                drawer.requestLayout();
                // Si quieres forzar CSS también:
                drawer.applyCss();
            });
        });

        // Permitir overlay y click fuera para cerrar
        drawer.setOnDrawerOpening(e -> {
            drawer.setMouseTransparent(false);
            hamburger.toFront();
        });
        drawer.setOverLayVisible(true);

        initDrawerContent();
        loadGastosMensuales();
    }

    // Esta función se llamará manualmente desde SceneChanger
    public void postInitialize(Stage stage) {
        stage.setMinWidth(720);
        stage.setMinHeight(480);

        // Asegurar que el hamburger esté siempre visible después de la inicialización
        hamburger.toFront();
        hamburger.setViewOrder(-1.0);
    }

    private void initDrawerContent() {
        VBox menu = new VBox(10);
        menu.setStyle("-fx-padding: 10; -fx-background-color: white;");

        VBox subMenu = new VBox(5);
        subMenu.setVisible(false);
        subMenu.setStyle("-fx-padding: 0 0 0 15;");

        Button exportBtn = new Button("Exportar a .xlsx");
        Button importBtn = new Button("Importar desde .xlsx");
        styleMenuButton(exportBtn);
        styleMenuButton(importBtn);
        subMenu.getChildren().addAll(exportBtn, importBtn);

        Button archivoBtn = new Button("Archivo");
        styleMenuButton(archivoBtn);
        archivoBtn.setOnMouseEntered(e -> subMenu.setVisible(true));
        archivoBtn.setOnMouseExited(e -> subMenu.setVisible(false));
        subMenu.setOnMouseExited(e -> subMenu.setVisible(false));

        menu.getChildren().addAll(archivoBtn, subMenu);

        for (String txt : new String[]{"Añadir Gasto", "Añadir Ingreso", "Cuentas", "Presupuesto"}) {
            Button btn = new Button(txt);
            styleMenuButton(btn);
            menu.getChildren().add(btn);
        }

        drawer.setSidePane(menu);
    }

    private void styleMenuButton(Button btn) {
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #333; -fx-font-size: 14px;");
    }

    //CargarNombreCuenta
    private void cargarCuentaDelUsuario() {
        int idUsuario = Sesion.getUsuarioActual().getIdUsuario(); // ← asegúrate que esto existe
        cuentaActual = CuentaDAO.obtenerCuentaPorUsuario(idUsuario);

        if (cuentaActual != null) {
            tituloCuenta.setText(cuentaActual.getNombre());
        } else {
            tituloCuenta.setText("Cuenta no encontrada");
        }
    }

    private int[] obtenerGastosPorMesDesdeBD() {
        int[] gastos = new int[12];

        // Aquí deberías hacer una consulta a la BD para obtener el total de gastos por mes
        // Por ahora es un ejemplo con datos aleatorios
        gastos[0] = 320;
        gastos[1] = 210;
        gastos[2] = 420;
        gastos[3] = 760;
        gastos[4] = 900;
        gastos[5] = 180;
        gastos[6] = 1000;
        gastos[7] = 500;
        gastos[8] = 620;
        gastos[9] = 400;
        gastos[10] = 200;
        gastos[11] = 880;

        return gastos;
    }

    private void loadGastosMensuales() {
        XYChart.Series<String, Number> gastosSeries = new XYChart.Series<>();
        gastosSeries.setName("Gastos");

        // Supongamos que estos son los datos provisionales para mostrar (test o demo)
        int[] gastosPorMes = obtenerGastosPorMesDesdeBD(); // del 0 (enero) al 11 (diciembre)
        String[] nombresMeses = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"};

        for (int i = 0; i < 12; i++) {
            gastosSeries.getData().add(new XYChart.Data<>(nombresMeses[i], gastosPorMes[i]));
        }

        gastoMensualChart.getData().clear();
        gastoMensualChart.getData().add(gastosSeries);
        gastoMensualChart.setLegendVisible(false);
    }

    @FXML
    private void handleUserIcon() {
        Stage mainStage = (Stage) tituloCuenta.getScene().getWindow();

        // Obtener datos del usuario desde la nueva sesión
        String nombre = Sesion.getUsuarioActual().getNombre();
        String email = Sesion.getUsuarioActual().getEmail();

        SceneChanger.openFXMLPopup("/org/example/savemate/fxml/UserInfo.fxml", "Cuenta Iniciada",
                (controller, stage) -> {
                    UserInfoController userInfo = (UserInfoController) controller;
                    userInfo.initData(stage, mainStage, nombre, email);
                });
    }

    @FXML
    private void onTotalGastadoClick() {
        System.out.println("Total Gastado clicado.");
        // Aquí puedes abrir una nueva vista, mostrar más info, etc.
    }

    @FXML
    private void onTotalIngresadoClick() {
        System.out.println("Total Ingresado clicado.");
    }

    @FXML
    private void onSaldoActualClick() {
        System.out.println("Saldo Actual clicado.");
    }
}