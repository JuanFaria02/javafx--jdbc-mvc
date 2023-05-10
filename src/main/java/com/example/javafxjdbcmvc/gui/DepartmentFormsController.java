package com.example.javafxjdbcmvc.gui;

import com.example.javafxjdbcmvc.gui.util.Constraints;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.net.URL;
import java.nio.Buffer;
import java.util.ResourceBundle;

public class DepartmentFormsController implements Initializable {
    @FXML
    private TextField textFieldId;
    @FXML
    private TextField textFieldName;
    @FXML
    private Label textErrorName;
    @FXML
    private Button buttonSave;
    @FXML
    private Button buttonCancel;
    @FXML
    public void onButtonSaveAction(){
        System.out.println("Save");
    }
    @FXML
    public void onButtonCancelAction(){
        System.out.println("Cancel");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeNodes();
    }

    private void initializeNodes(){
        Constraints.setTextFieldInteger(textFieldId);
        Constraints.setTextFieldMaxLength(textFieldName, 30);

    }
}
