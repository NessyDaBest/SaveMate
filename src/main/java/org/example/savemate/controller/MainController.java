package org.example.savemate.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.transitions.hamburger.HamburgerSlideCloseTransition;

import org.example.savemate.database.CuentaDAO;
import org.example.savemate.database.PresupuestoDAO;
import org.example.savemate.model.Cuenta;
import org.example.savemate.util.SceneChanger;
import org.example.savemate.util.Sesion;

import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

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
    @FXML private Label labelExcesos;

    private HamburgerSlideCloseTransition transition;
    private Cuenta cuentaActual;

    @FXML
    public void initialize() {
        //tituloCuenta.setText("Cuenta Ahorros");
        // Obtener y mostrar cuenta del usuario
        cargarCuentaDelUsuario();
        configurarFiltroPorAnio();

        //titulo de cuenta clicable
        tituloCuenta.setOnMouseClicked(e -> {
            SceneChanger.changeScene(
                    (Stage) tituloCuenta.getScene().getWindow(),
                    "/org/example/savemate/fxml/Cuentas.fxml",
                    "Cuentas bancarias"
            );
        });

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
        String[] opciones = {"Inicio", "Gastos", "Ingresos", "Cuentas", "Limites"};

        for (String txt : opciones) {
            Button btn = new Button(txt);
            styleMenuButton(btn); // Método estático o interno de MainController

            btn.setOnAction(e -> {
                Stage ventanaActual = (Stage) tituloCuenta.getScene().getWindow();

                switch (txt) {
                    case "Inicio" -> SceneChanger.changeScene(
                            ventanaActual,
                            "/org/example/savemate/fxml/Main.fxml",
                            "SaveMate - Principal"
                    );

                    case "Gastos" -> SceneChanger.changeScene(
                            ventanaActual,
                            "/org/example/savemate/fxml/ListadoGastos.fxml",
                            "Listado de Gastos"
                    );

                    case "Ingresos" -> SceneChanger.changeScene(
                            ventanaActual,
                            "/org/example/savemate/fxml/ListadoIngresos.fxml",
                            "Listado de Ingresos"
                    );
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
        cuentaActual = Sesion.getCuentaActual();
        System.out.println("DEBUG: cuentaActual = " + cuentaActual);

        if (cuentaActual != null) {
            cuentaActual.setSaldoInicial(CuentaDAO.obtenerSaldoInicial(cuentaActual.getIdCuenta()));
            tituloCuenta.setText(cuentaActual.getNombre());
        } else {
            tituloCuenta.setText("Cuenta no encontrada");
        }
    }
    //Combo box para el filtro por año
    private void configurarFiltroPorAnio() {
        int añoActual = java.time.Year.now().getValue();

        if (cuentaActual == null) {
            // Usuario sin cuenta → dejar el combo deshabilitado y mostrar un año por defecto
            añoComboBox.getItems().clear();
            añoComboBox.getItems().add(String.valueOf(añoActual)); // puedes poner "2025" si quieres
            añoComboBox.setValue(String.valueOf(añoActual));
            añoComboBox.setDisable(true); // DESHABILITAR EL COMBO
            return;
        }

        añoComboBox.setDisable(false); // Habilitar si sí hay cuenta

        int minGasto = CuentaDAO.obtenerMinAñoGastos(cuentaActual.getIdCuenta());
        int maxGasto = CuentaDAO.obtenerMaxAñoGastos(cuentaActual.getIdCuenta());
        int minIngreso = CuentaDAO.obtenerMinAñoIngresos(cuentaActual.getIdCuenta());
        int maxIngreso = CuentaDAO.obtenerMaxAñoIngresos(cuentaActual.getIdCuenta());

        int minYear = Math.min(
                minGasto == 0 ? añoActual : minGasto,
                minIngreso == 0 ? añoActual : minIngreso
        );
        int maxYear = Math.max(
                maxGasto == 0 ? añoActual : maxGasto,
                maxIngreso == 0 ? añoActual : maxIngreso
        );

        // Seguridad: si la cuenta no tiene nada, mostrar al menos 2022..añoActual
        minYear = Math.min(minYear, 2022);
        maxYear = Math.max(maxYear, añoActual);

        añoComboBox.getItems().clear();
        for (int año = minYear; año <= maxYear; año++) {
            añoComboBox.getItems().add(String.valueOf(año));
        }

        añoComboBox.setValue(String.valueOf(maxYear)); // Por defecto el más reciente

        añoComboBox.setOnAction(e -> {
            loadMonto(); // Recarga el gráfico con el año nuevo
        });
    }

    private void loadMonto() {
        if (cuentaActual == null) return;

        int añoSeleccionado = Integer.parseInt(añoComboBox.getValue());
        Map<Integer, Double> gastosPorMes = CuentaDAO.obtenerGastosMensuales(cuentaActual.getIdCuenta(), añoSeleccionado);
        Map<Integer, Double> ingresosPorMes = CuentaDAO.obtenerIngresosMensuales(cuentaActual.getIdCuenta(), añoSeleccionado);
        Map<Integer, Double> limitesPorMes = PresupuestoDAO.obtenerPresupuestosPorCuentaYAño(cuentaActual.getIdCuenta(), añoSeleccionado);

        XYChart.Series<String, Number> gastosSeries = new XYChart.Series<>();
        XYChart.Series<String, Number> ingresosSeries = new XYChart.Series<>();

        gastosSeries.setName("Gastos");
        ingresosSeries.setName("Ingresos");

        AtomicInteger totalExcesos = new AtomicInteger(0);
        for (int i = 1; i <= 12; i++) {
            final int mesIndex = i;
            String mes = String.valueOf(mesIndex);
            double gasto = gastosPorMes.getOrDefault(mesIndex, 0.0);
            double ingreso = ingresosPorMes.getOrDefault(mesIndex, 0.0);
            Double limite = limitesPorMes.get(mesIndex); // null si no hay límite

            final double gastoFinal = gasto;
            final Double limiteFinal = limite; // puede ser null

            // GASTOS
            XYChart.Data<String, Number> dataGasto = new XYChart.Data<>(mes, gastoFinal);
            gastosSeries.getData().add(dataGasto);
            dataGasto.nodeProperty().addListener((obs, oldNode, newNode) -> {
                if (newNode != null) {
                    boolean excede = limiteFinal != null && gastoFinal > limiteFinal;
                    String color = excede ? "#8B0000" : "#C75B5B";
                    newNode.setStyle("-fx-bar-fill: " + color + ";");

                    String textoTooltip = "Gasto Mes " + mesIndex + ": " + String.format("%.2f", gastoFinal) + " €";
                    if (limiteFinal != null) {
                        textoTooltip += "\nLímite: " + String.format("%.2f", limiteFinal) + " €";
                        if (excede) {
                            textoTooltip += "\n¡Excede el límite por " + String.format("%.2f", gastoFinal - limiteFinal) + " €!";
                        }
                    }
                    if (limiteFinal != null && gastoFinal > limiteFinal) {
                        totalExcesos.incrementAndGet();
                    }
                    Tooltip.install(newNode, new Tooltip(textoTooltip));
                }
            });

            // INGRESOS
            XYChart.Data<String, Number> dataIngreso = new XYChart.Data<>(mes, ingreso);
            ingresosSeries.getData().add(dataIngreso);
            dataIngreso.nodeProperty().addListener((obs, oldNode, newNode) -> {
                if (newNode != null) {
                    newNode.setStyle("-fx-bar-fill: #75BB8B;");
                    Tooltip.install(newNode, new Tooltip("Ingreso Mes " + mesIndex + ": " + String.format("%.2f", ingreso) + " €"));
                }
            });
        }

        gastoMensualChart.getData().clear();
        gastoMensualChart.getData().addAll(gastosSeries, ingresosSeries);
        gastoMensualChart.setLegendVisible(false);

        // Totales
        double totalGastos = CuentaDAO.obtenerTotalGastosAnuales(cuentaActual.getIdCuenta(), añoSeleccionado);
        double totalIngresos = CuentaDAO.obtenerTotalIngresosAnuales(cuentaActual.getIdCuenta(), añoSeleccionado);
        double gastosAcumulados = CuentaDAO.obtenerTotalGastosAcumulado(cuentaActual.getIdCuenta());
        double ingresosAcumulados = CuentaDAO.obtenerTotalIngresosAcumulado(cuentaActual.getIdCuenta());
        double saldo = cuentaActual.getSaldoInicial() + ingresosAcumulados - gastosAcumulados;

        labelGasto.setText(String.format("%.2f €", totalGastos));
        labelIngreso.setText(String.format("%.2f €", totalIngresos));
        labelSaldo.setText(String.format("%.2f €", saldo));
        labelExcesos.setText(String.valueOf(totalExcesos));
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
    @FXML
    private void onLimitesExcedidosClick() {
        SceneChanger.changeScene(
                (Stage) tituloCuenta.getScene().getWindow(),
                "/org/example/savemate/fxml/Presupuesto.fxml",
                "Límites bancarios"
        );
    }
}