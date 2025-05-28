package org.example.savemate.util;

import javafx.stage.Stage;

@FunctionalInterface
public interface ControllerInitializer {
    void init(Object controller, Stage stage);
}