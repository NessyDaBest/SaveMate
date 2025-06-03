package org.example.savemate.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
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

import java.util.Map;

public class MainController {

    @FXML private JFXDrawer drawer;
    @FXML private JFXHamburger hamburger;
    @FXML private Button userButton;
    @FXML private Label tituloCuenta;
    @FXML private Label labelGasto;
    @FXML private Label labelIngreso;
    @FXML private Label labelSaldo;
    @FXML private BarChart<String, Number> gastoMensualChart;
    @FXML private ComboBox<String> añoComboBox;

    private HamburgerSlideCloseTransition transition;
    private Cuenta cuentaActual;

    @FXML
    public void initialize() {
        //tituloCuenta.setText("Cuenta Ahorros");
        // Obtener y mostrar cuenta del usuario
        cargarCuentaDelUsuario();
        configurarFiltroPorAño();

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
        loadMonto();
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

        // Lista de opciones del drawer
        String[] opciones = {"Inicio", "Añadir Gasto", "Añadir Ingreso", "Crear Cuenta", "Presupuesto"};

        for (String txt : opciones) {
            Button btn = new Button(txt);
            styleMenuButton(btn);

            // Aquí puedes definir el comportamiento de cada botón según su texto
            btn.setOnAction(e -> {
                switch (txt) {
                    case "Inicio" -> {
                        SceneChanger.changeScene(
                                (Stage) tituloCuenta.getScene().getWindow(),
                                "/org/example/savemate/fxml/Main.fxml",
                                "SaveMate - Principal"
                        );
                    }
                    case "Gasto" -> System.out.println("Navegar a Añadir Gasto");
                    case "Ingreso" -> System.out.println("Navegar a Añadir Ingreso");
                    case "Crear Cuenta" -> System.out.println("Navegar a Crear Cuenta");
                    case "Presupuesto" -> System.out.println("Navegar a Presupuesto");
                }
            });

            menu.getChildren().add(btn);
        }

        drawer.setSidePane(menu);
    }

    public static void styleMenuButton(Button btn) {
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setStyle("""
        -fx-background-color: transparent;
        -fx-background-radius: 6px;
        -fx-padding: 6px;
        -fx-cursor: hand;
        -fx-text-fill: #333;
        -fx-font-size: 14px;
    """);

        //Efecto :Hover
        btn.setOnMouseEntered(e -> btn.setStyle("""
        -fx-background-color: #eaeaea;
        -fx-background-radius: 6px;
        -fx-padding: 6px;
        -fx-cursor: hand;
        -fx-text-fill: #333;
        -fx-font-size: 14px;
    """));
        btn.setOnMouseExited(e -> btn.setStyle("""
        -fx-background-color: transparent;
        -fx-background-radius: 6px;
        -fx-padding: 6px;
        -fx-cursor: hand;
        -fx-text-fill: #333;
        -fx-font-size: 14px;
    """));
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
    //Combo box para el filtro por año
    private void configurarFiltroPorAño() {
        int añoActual = java.time.Year.now().getValue();

        // Rango de años desde 2022 hasta el año actual
        for (int año = 2022; año <= añoActual; año++) {
            añoComboBox.getItems().add(String.valueOf(año));
        }

        añoComboBox.setValue(String.valueOf(añoActual)); // Año seleccionado por defecto

        añoComboBox.setOnAction(e -> {
            loadMonto(); // Recarga el gráfico con el año nuevo
        });
    }

    private void loadMonto() {
        if (cuentaActual == null) return;

        int añoSeleccionado = Integer.parseInt(añoComboBox.getValue());
        Map<Integer, Double> gastosPorMes = CuentaDAO.obtenerGastosMensuales(cuentaActual.getIdCuenta(), añoSeleccionado);
        Map<Integer, Double> ingresosPorMes = CuentaDAO.obtenerIngresosMensuales(cuentaActual.getIdCuenta(), añoSeleccionado);

        XYChart.Series<String, Number> gastosSeries = new XYChart.Series<>();
        gastosSeries.setName("Gastos");

        XYChart.Series<String, Number> ingresosSeries = new XYChart.Series<>();
        ingresosSeries.setName("Ingresos");

        for (int i = 1; i <= 12; i++) {
            String mes = String.valueOf(i);

            // Gastos
            double gasto = gastosPorMes.getOrDefault(i, 0.0);
            XYChart.Data<String, Number> dataGasto = new XYChart.Data<>(mes, gasto);
            gastosSeries.getData().add(dataGasto);
            Tooltip tooltipGasto = new Tooltip("Gasto Mes " + i + ": " + String.format("%.2f", gasto) + " €");
            dataGasto.nodeProperty().addListener((obs, oldNode, newNode) -> {
                if (newNode != null) {
                    newNode.setStyle("-fx-bar-fill: #C73C31;"); // rojo
                    Tooltip.install(newNode, tooltipGasto);
                }
            });

            // Ingresos
            double ingreso = ingresosPorMes.getOrDefault(i, 0.0);
            XYChart.Data<String, Number> dataIngreso = new XYChart.Data<>(mes, ingreso);
            ingresosSeries.getData().add(dataIngreso);
            Tooltip tooltipIngreso = new Tooltip("Ingreso Mes " + i + ": " + String.format("%.2f", ingreso) + " €");
            dataIngreso.nodeProperty().addListener((obs, oldNode, newNode) -> {
                if (newNode != null) {
                    newNode.setStyle("-fx-bar-fill: #75BB8B;"); // verde
                    Tooltip.install(newNode, tooltipIngreso);
                }
            });
        }
        // Obtener totales y actualizar labels
        double totalGastos = CuentaDAO.obtenerTotalGastosAnuales(cuentaActual.getIdCuenta(), añoSeleccionado);
        double totalIngresos = CuentaDAO.obtenerTotalIngresosAnuales(cuentaActual.getIdCuenta(), añoSeleccionado);
        // El saldo se calcula con todos los años
        double gastosAcumulados = CuentaDAO.obtenerTotalGastosAcumulado(cuentaActual.getIdCuenta());
        double ingresosAcumulados = CuentaDAO.obtenerTotalIngresosAcumulado(cuentaActual.getIdCuenta());
        double saldo = ingresosAcumulados - gastosAcumulados;

        // Mostrar formateado
        labelGasto.setText(String.format("%.2f €", totalGastos));
        labelIngreso.setText(String.format("%.2f €", totalIngresos));
        labelSaldo.setText(String.format("%.2f €", saldo));

        gastoMensualChart.getData().clear();
        gastoMensualChart.getData().addAll(gastosSeries, ingresosSeries);
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

                    // Hacer que el popup sea modal y bloquee la interacción con la ventana principal
                    stage.initOwner(mainStage);
                    stage.setResizable(false);
                    stage.initModality(javafx.stage.Modality.WINDOW_MODAL);
                    stage.setAlwaysOnTop(true); // opcional, para que siempre esté al frente
                });
    }

    @FXML
    private void onTotalGastadoClick() {
        SceneChanger.changeScene(
                (Stage) tituloCuenta.getScene().getWindow(),
                "/org/example/savemate/fxml/ListadoGastos.fxml",
                "Listado de Gastos"
        );
    }

    @FXML
    private void onTotalIngresadoClick() {
        SceneChanger.changeScene(
                (Stage) tituloCuenta.getScene().getWindow(),
                "/org/example/savemate/fxml/ListadoIngresos.fxml",
                "Listado de Ingresos"
        );
    }

    @FXML
    private void onSaldoActualClick() {
        SceneChanger.changeScene(
                (Stage) tituloCuenta.getScene().getWindow(),
                "/org/example/savemate/fxml/ListadoMovimientos.fxml",
                "Listado de Movimientos"
        );
    }
}