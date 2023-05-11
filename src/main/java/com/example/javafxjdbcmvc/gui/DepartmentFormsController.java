package com.example.javafxjdbcmvc.gui;

import com.example.javafxjdbcmvc.db.exceptions.DbException;
import com.example.javafxjdbcmvc.gui.util.Alerts;
import com.example.javafxjdbcmvc.gui.util.Constraints;
import com.example.javafxjdbcmvc.gui.util.Utils;
import com.example.javafxjdbcmvc.model.entities.Department;
import com.example.javafxjdbcmvc.model.services.DepartmentService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class DepartmentFormsController implements Initializable {
    private DepartmentService departmentService;
    private Department department;
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
    public void onButtonSaveAction(ActionEvent event){
        if (department == null){
            throw new IllegalStateException("Entity is null");
        }
        if (departmentService == null){
            throw new IllegalStateException("Department Service is null");
        }
        try {
            department = getFormData(); //Vai pegar os dados do text field e implementar no department
            departmentService.saveOrUpdate(department);
            Utils.currentStage(event).close();
        }
        catch (DbException e){
            Alerts.showAlert("Error saving object", null, e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    @FXML
    public void onButtonCancelAction(ActionEvent event){
        Utils.currentStage(event).close();
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public void setDepartmentService(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    public void updateFormData(){
        if (department == null){
            throw new IllegalStateException("Entity was null");
        }

        textFieldId.setText(String.valueOf(department.getId()));
        textFieldName.setText(verifyIfNameIsNull(department.getName()));
    }

    private String verifyIfNameIsNull(String name){
        return  (department.getName() != null) ? name : "";
    }

    private Department getFormData(){
        Department obj = new Department();
        obj.setId(Utils.tryParseToInt(textFieldId.getText()));
        obj.setName(textFieldName.getText());
        return obj;
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
