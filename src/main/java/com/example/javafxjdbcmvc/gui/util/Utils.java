package com.example.javafxjdbcmvc.gui.util;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.stage.Stage;

public class Utils {
    public static Stage currentStage(ActionEvent actionEvent){
        //retorna o stage aonde o controller que recebeu o evento está
        return (Stage) ((Node)actionEvent.getSource()).getScene().getWindow();
    }

}
