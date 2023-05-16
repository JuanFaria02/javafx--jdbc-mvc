package com.example.javafxjdbcmvc.gui;

import com.example.javafxjdbcmvc.db.exceptions.DbException;
import com.example.javafxjdbcmvc.gui.listeners.DataChangeListener;
import com.example.javafxjdbcmvc.gui.util.Alerts;
import com.example.javafxjdbcmvc.gui.util.Constraints;
import com.example.javafxjdbcmvc.gui.util.Utils;
import com.example.javafxjdbcmvc.model.entities.Seller;
import com.example.javafxjdbcmvc.model.exceptions.ValidationException;
import com.example.javafxjdbcmvc.model.services.SellerService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.*;

public class SellerFormsController implements Initializable {
    private List<DataChangeListener> dataChangeListeners = new ArrayList<>();
    private SellerService sellerService;
    private Seller seller;
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

    public void setSellerService(SellerService SellerService) {
        this.sellerService = SellerService;
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
        Constraints.setTextFieldMaxLength(textFieldName, 30);

    }
}
