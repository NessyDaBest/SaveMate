module org.example.savemate {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;

    //BD
    requires java.sql;

    //Encriptador
    requires jbcrypt;

    //JFoenix
    requires com.jfoenix;

    // Permitir acceso desde FXML
    opens org.example.savemate to javafx.fxml;
    opens org.example.savemate.controller to javafx.fxml;

    exports org.example.savemate;
    exports org.example.savemate.controller;
}