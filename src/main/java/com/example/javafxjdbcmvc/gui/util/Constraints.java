package com.example.javafxjdbcmvc.gui.util;

import javafx.scene.control.TextField;


public class Constraints {
    //Faz que a coixa de texto permita apenas inteiros
    public static void setTextFieldInteger(TextField textField){
        textField.textProperty().addListener(((observableValue, oldValue, newValue) -> {
            //se o valor que está sendo digitado não for null e diferente de inteiro então não será possível digitar
            if (newValue != null && !newValue.matches("\\d*")){//regex para inteiro
                textField.setText(oldValue);
            }
        }));
    }
    //Faz que a coixa de texto permita apenas double

    public static void setTextFieldDouble(TextField textField){
        textField.textProperty().addListener((observableValue, oldValue, newValue) -> {
            //se o valor que está sendo digitado não for nulo e diferente de double então não será possível digitar

            if (newValue != null && !newValue.matches("\\d*([\\.]\\d*)?")) {//regex para double
                textField.setText(oldValue);
            }
        });
    }
    //Faz que a coixa de texto permita apenas string
    public static void setTextFieldMaxLength(TextField textField, int max){
        textField.textProperty().addListener(((observableValue, oldValue, newValue) -> {
            if (newValue!=null && newValue.length() > max){
                textField.setText(oldValue);
            }
        }));
    }
}
