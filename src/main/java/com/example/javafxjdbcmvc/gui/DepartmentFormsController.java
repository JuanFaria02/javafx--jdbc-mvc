package com.example.javafxjdbcmvc.gui;

import com.example.javafxjdbcmvc.db.exceptions.DbException;
import com.example.javafxjdbcmvc.gui.listeners.DataChangeListener;
import com.example.javafxjdbcmvc.gui.util.Alerts;
import com.example.javafxjdbcmvc.gui.util.Constraints;
import com.example.javafxjdbcmvc.gui.util.Utils;
import com.example.javafxjdbcmvc.model.entities.Department;
import com.example.javafxjdbcmvc.model.exceptions.ValidationException;
import com.example.javafxjdbcmvc.model.services.DepartmentService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.*;

public class DepartmentFormsController implements Initializable {
    private List<DataChangeListener> dataChangeListeners = new ArrayList<>();
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


    // BOTÕES

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
            notifyDataChangeListener();
            Utils.currentStage(event).close();
        }
        catch (DbException e){
            Alerts.showAlert("Error saving object", null, e.getMessage(), Alert.AlertType.ERROR);
        }
        catch (ValidationException e){
            setErrorMessages(e.getErrors());
        }
    }

    @FXML
    public void onButtonCancelAction(ActionEvent event){
        Utils.currentStage(event).close();
    }


    //---------------------------------------------------------------------//

    // INICIALIZAÇÃO

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeNodes();
    }


    private void initializeNodes(){
        Constraints.setTextFieldInteger(textFieldId);
        Constraints.setTextFieldMaxLength(textFieldName, 30);
    }

    //------------------------------------------------------------//

    //  MAIN FORMULARIO
    public void updateFormData(){
        if (department == null){
            throw new IllegalStateException("Entity was null");
        }

        textFieldId.setText(String.valueOf(department.getId()));
        textFieldName.setText(verifyIfNameIsNull(department.getName()));
    }


    //-------------------------------------------------------------//

    //  UTILITARIOS

    public void notifyDataChangeListener(){
        for (DataChangeListener listener:dataChangeListeners){
            listener.onDataChanged();
        }
    }

    //Vai adicionar outros objetos na lista
    public void subscriteDataChangeListener(DataChangeListener listener){
        dataChangeListeners.add(listener);
    }


    private String verifyIfNameIsNull(String name){
        return  (department.getName() != null) ? name : "";
    }


    //--------------------------------------------------------------------//

    //  GETTERS AND SETTERS
    public void setDepartment(Department department) {
        this.department = department;
    }

    public void setDepartmentService(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    private void setErrorMessages(Map<String, String> errorMessages){
        Set<String> fields = errorMessages.keySet();
        if (fields.contains("Name")){
            textErrorName.setText(errorMessages.get("Name"));
        }
    }
    private Department getFormData(){
        Department obj = new Department();

        ValidationException exception = new ValidationException("Validation error"); //Instaciou a exceção
        obj.setId(Utils.tryParseToInt(textFieldId.getText()));

        if (textFieldName.getText() == null || textFieldName.getText().trim().equals("")){
            exception.addErrors("Name", "Field can't be empty");
        }

        obj.setName(textFieldName.getText());

        if (exception.getErrors().size()>0){
            throw exception;
        }
        return obj;
    }

}
