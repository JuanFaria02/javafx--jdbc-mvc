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
import java.time.LocalDate;
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
    private Label textErrorBirthDate;
    @FXML
    private Label textErrorEmail;
    @FXML
    private Label textErrorBaseSalary;
    @FXML
    private Button buttonSave;
    @FXML
    private Button buttonCancel;



    // BOTÕES


    //Ação do botão de salvar vendedor
    @FXML
    public void onButtonSaveAction(ActionEvent event){
        //Programação defensiva para caso o programador esqueça de instanciar o objeto
        if (seller == null){
            throw new IllegalStateException("Entity is null");
        }
        if (sellerService == null){
            throw new IllegalStateException("Seller Service is null");
        }
        try {
            //Vai pegar os dados do text field e jogar no Seller
            seller = getFormData();
            //Salva ou atualiza
            sellerService.saveOrUpdate(seller);
            //Notifica que o dado foi atualizado ou inserido
            notifyDataChangeListener();
            //Fecha o estado atual que é o formulário
            Utils.currentStage(event).close();
        }
        catch (DbException e){
            Alerts.showAlert("Error saving object", null, e.getMessage(), Alert.AlertType.ERROR);
        }
        catch (ValidationException e){
            setErrorMessages(e.getErrors());
        }
    }

    //Botão para canelar a inserção ou atualização
    @FXML
    public void onButtonCancelAction(ActionEvent event){
        //Fecha o formulário
        Utils.currentStage(event).close();
    }

    //--------------------------------------------------------------------------//

    //  INICIALIZAÇÃO

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //Inicializa o nó assim que o controlador é chamado
        initializeNodes();
    }


    private void initializeNodes(){
        //Faz o id não permitir outros caracteres a não ser inteiros
        Constraints.setTextFieldInteger(textFieldId);
        //Faz o nome não permitir ficar vazio e nem passar de 70 caracteres
        Constraints.setTextFieldMaxLength(textFieldName, 70);
        //Faz o salário não permitir outros caracteres a não ser double
        Constraints.setTextFieldDouble(baseSalary);
        //Faz o email não permitir ficar vazio e nem passar de 60 caracteres
        Constraints.setTextFieldMaxLength(textFieldEmail, 60);
        //Faz o aniversário ser formatado dessa maneira
        Utils.formatDatePicker(birthDate, "dd/MM/yyyy");
        //Inicializa o comboBox do departamento do vendedor
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

    //---------------------------------------------------------------------//

    //  MAIN DO FORMULÁRIO

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

    //---------------------------------------------------------------------//

    //  UTILITÁRIOS DO FORMULÁRIO
    //Vai adicionar outros objetos na lista
    public void subscriteDataChangeListener(DataChangeListener listener){
        //Adiciona na lista que o
        dataChangeListeners.add(listener);
    }

    //Carrega os departamentos associados ao vendedor
    public void loadAssociateObjects(){
        //Pega a lista de departamentos
        List<Department> list = departmentService.findAll();
        //Adiciona na observable list
        departmentObservableList = FXCollections.observableArrayList(list);
        //Coloca os itens na comboBox
        departmentComboBox.setItems(departmentObservableList);
    }

    private String verifyIfNameIsNull(String name){
        //Verifica se o nome é null
        return  (seller.getName() != null) ? name : "";
    }


    //Notifica que o dado mudou
    public void notifyDataChangeListener(){
        //Chama a função que notifica que o dado mudou
        for (DataChangeListener listener:dataChangeListeners){
            listener.onDataChanged();
        }
    }

    private void makeValidation(){
        ValidationException exception = new ValidationException("Validation error"); //Instaciou a exceção

        if (textFieldName.getText() == null || textFieldName.getText().trim().equals("")){
            exception.addErrors("Name", "Field can't be empty");
        }

        if (textFieldEmail.getText() == null || textFieldEmail.getText().trim().equals("")){
            exception.addErrors("Email", "Field can't be empty");
        }
        if (baseSalary.getText() == null || baseSalary.getText().trim().equals("")){
            exception.addErrors("baseSalary", "Field can't be empty");
        }
        if (birthDate.getValue() == null){
            exception.addErrors("birthDate", "Field can't be empty");
        }
        if (exception.getErrors().size()>0){
            throw exception;
        }
    }
    //---------------------------------------------------------------------//

    //   GETTERS AND SETTERS


    public void setSeller(Seller seller) {
        this.seller = seller;
    }

    public void setServices(SellerService SellerService, DepartmentService departmentService) {
        this.sellerService = SellerService;
        this.departmentService = departmentService;
    }

    private void setErrorMessages(Map<String, String> errorMessages){
        Set<String> fields = errorMessages.keySet();

        textErrorName.setText((fields.contains("Name")?errorMessages.get("Name"):""));
        textErrorEmail.setText((fields.contains("Email")?errorMessages.get("Email"):""));
        textErrorBaseSalary.setText((fields.contains("baseSalary")?errorMessages.get("baseSalary"):""));
        textErrorBirthDate.setText((fields.contains("birthDate")?errorMessages.get("birthDate"):""));


    }
    private Seller getFormData(){
        Seller obj = new Seller();

        obj.setId(Utils.tryParseToInt(textFieldId.getText()));
        makeValidation();

        obj.setName(textFieldName.getText());
        obj.setEmail(textFieldEmail.getText());
        obj.setBirthDate(birthDate.getValue());
        obj.setBaseSalary(Utils.tryParseToDouble(baseSalary.getText()));
        obj.setDepartment(departmentComboBox.getValue());
        return obj;
    }

}
