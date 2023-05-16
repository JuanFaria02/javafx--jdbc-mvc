package com.example.javafxjdbcmvc.gui;

import com.example.javafxjdbcmvc.db.exceptions.DbException;
import com.example.javafxjdbcmvc.gui.listeners.DataChangeListener;
import com.example.javafxjdbcmvc.gui.util.Alerts;
import com.example.javafxjdbcmvc.gui.util.Constraints;
import com.example.javafxjdbcmvc.gui.util.Utils;
import com.example.javafxjdbcmvc.model.entities.Department;
import com.example.javafxjdbcmvc.model.entities.Seller;
import com.example.javafxjdbcmvc.model.exceptions.ValidationException;
import com.example.javafxjdbcmvc.model.services.DepartmentService;
import com.example.javafxjdbcmvc.model.services.SellerService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.util.Callback;

import java.net.URL;
import java.util.*;

public class SellerFormsController implements Initializable {
    private List<DataChangeListener> dataChangeListeners = new ArrayList<>();
    private SellerService sellerService;
    private Seller seller;

    private DepartmentService departmentService;
    @FXML
    private TextField textFieldId;
    @FXML
    private TextField textFieldName;
    @FXML
    private TextField textFieldEmail;
    @FXML
    private DatePicker birthDate; //Data na aplicação
    @FXML
    private TextField baseSalary;
    @FXML
    private Label textErrorName;
    @FXML
    private ComboBox<Department> departmentComboBox;
    @FXML
    private ObservableList<Department> departmentObservableList;
    @FXML
    private Label textErrorEmail;
    @FXML
    private Label textErrorBithDate;
    @FXML
    private Label textErrorBaseSalary;
    @FXML
    private Button buttonSave;
    @FXML
    private Button buttonCancel;
    @FXML
    public void onButtonSaveAction(ActionEvent event){
        if (seller == null){
            throw new IllegalStateException("Entity is null");
        }
        if (sellerService == null){
            throw new IllegalStateException("Seller Service is null");
        }
        try {
            seller = getFormData(); //Vai pegar os dados do text field e implementar no Seller
            sellerService.saveOrUpdate(seller);
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

    public void notifyDataChangeListener(){
        for (DataChangeListener listener:dataChangeListeners){
            listener.onDataChanged();
        }
    }
    @FXML
    public void onButtonCancelAction(ActionEvent event){
        Utils.currentStage(event).close();
    }

    public void setSeller(Seller seller) {
        this.seller = seller;
    }

    public void setServices(SellerService SellerService, DepartmentService departmentService) {
        this.sellerService = SellerService;
        this.departmentService = departmentService;
    }
    //Vai adicionar outros objetos na lista
    public void subscriteDataChangeListener(DataChangeListener listener){
        dataChangeListeners.add(listener);
    }

    public void updateFormData(){
        if (seller == null){
            throw new IllegalStateException("Entity was null");
        }

        textFieldId.setText(String.valueOf(seller.getId()));
        textFieldName.setText(verifyIfNameIsNull(seller.getName()));
        textFieldEmail.setText(seller.getEmail());
        Locale.setDefault(Locale.US); //Colocar . e não ,
        baseSalary.setText(String.format("%.2f", seller.getBaseSalary()));
        birthDate.setValue(seller.getBirthDate());
        if (seller.getDepartment()==null){
            departmentComboBox.getSelectionModel().selectFirst();
        }
        else {
            departmentComboBox.setValue(seller.getDepartment());
        }
    }


    public void loadAssociateObjects(){
        List<Department> list = departmentService.findAll();
        departmentObservableList = FXCollections.observableArrayList(list);
        departmentComboBox.setItems(departmentObservableList);
    }

    private String verifyIfNameIsNull(String name){
        return  (seller.getName() != null) ? name : "";
    }


    private void setErrorMessages(Map<String, String> errorMessages){
        Set<String> fields = errorMessages.keySet();
        if (fields.contains("Name")){
            textErrorName.setText(errorMessages.get("Name"));
        }
    }
    private Seller getFormData(){
        Seller obj = new Seller();

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
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeNodes();
    }


    private void initializeNodes(){
        Constraints.setTextFieldInteger(textFieldId);
        Constraints.setTextFieldMaxLength(textFieldName, 70);
        Constraints.setTextFieldDouble(baseSalary);
        Constraints.setTextFieldMaxLength(textFieldEmail, 60);
        Utils.formatDatePicker(birthDate, "dd/MM/yyyy");
        initializeComboBoxDepartment();
    }

    private void initializeComboBoxDepartment() {
        Callback<ListView<Department>, ListCell<Department>> factory = lv -> new ListCell<Department>() {
            @Override
            protected void updateItem(Department item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : item.getName());
            }
        };
        departmentComboBox.setCellFactory(factory);
        departmentComboBox.setButtonCell(factory.call(null));
    }
}
