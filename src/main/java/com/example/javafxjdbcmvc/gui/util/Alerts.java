package com.example.javafxjdbcmvc.gui.util;

import javafx.scene.control.Alert;

public class Alerts {

    public void showAlert(String title, String header, String content, Alert.AlertType alertType){
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.show();
    }
}
