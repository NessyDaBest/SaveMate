package org.example.savemate.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.transitions.hamburger.HamburgerSlideCloseTransition;

import org.example.savemate.util.SceneChanger;
import org.example.savemate.util.UserSession;

public class MainController {

    @FXML private JFXDrawer drawer;
    @FXML private JFXHamburger hamburger;
    @FXML private Button userButton;
    @FXML private Label tituloCuenta;
    @FXML private Label labelGasto;
    @FXML private Label labelIngreso;
    @FXML private Label labelSaldo;

    private HamburgerSlideCloseTransition transition;

    @FXML
    public void initialize() {
        tituloCuenta.setText("Cuenta Ahorros");

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

    @FXML
    private void handleUserIcon() {
        Stage mainStage = (Stage) tituloCuenta.getScene().getWindow();
        SceneChanger.openFXMLPopup("/org/example/savemate/fxml/UserInfo.fxml", "Cuenta Iniciada",
                (controller, stage) -> {
                    UserInfoController userInfo = (UserInfoController) controller;
                    userInfo.initData(stage, mainStage, UserSession.getNombre(), UserSession.getEmail());
                });
    }
}