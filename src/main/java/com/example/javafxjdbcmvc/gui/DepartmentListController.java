package com.example.javafxjdbcmvc.gui;

import com.example.javafxjdbcmvc.Application;
import com.example.javafxjdbcmvc.db.exceptions.DbException;
import com.example.javafxjdbcmvc.gui.listeners.DataChangeListener;
import com.example.javafxjdbcmvc.gui.util.Alerts;
import com.example.javafxjdbcmvc.gui.util.Utils;
import com.example.javafxjdbcmvc.model.entities.Department;
import com.example.javafxjdbcmvc.model.services.DepartmentService;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class DepartmentListController implements Initializable, DataChangeListener {
    @FXML
    private TableColumn<Department, Department> tableColumnDelete;
    @FXML
    private TableColumn<Department, Department> tableColumnEdit; //Table para edição
    @FXML
    private ObservableList<Department> observableList;
    private DepartmentService departmentService;
    @FXML
    private TableView<Department> tableViewDepartment;
    @FXML
    private TableColumn<Department, Integer> tableColumnId;
    @FXML
    private TableColumn<Department, String> tableColumnName;

    @FXML
    private Button buttonRegistrer;

    //BOTÕES

    @FXML
    public void onButtonRegistrerAction(ActionEvent event){ //pega a referência do controller que realizou o event
        Stage parentStage = Utils.currentStage(event);
        Department department = new Department();
        createDialogForm(department, "DepartmentForms.fxml", parentStage);
    }

    private void initDeleteButton(){
        tableColumnDelete.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
        tableColumnDelete.setCellFactory(param -> new TableCell<Department, Department>() {
            private final Button button = new Button("remove");
            @Override
            protected void updateItem(Department obj, boolean empty) {
                super.updateItem(obj, empty);
                if (obj == null) {
                    setGraphic(null);
                    return;
                }
                setGraphic(button);
                button.setOnAction(event -> removeEntity(obj));
            }
        });
    }

    private void removeEntity(Department obj){
        Optional<ButtonType> result = Alerts.showConfirmation("Confirmation", "Are you sure to delete?");
        if (result.get() == ButtonType.OK){
            if (departmentService == null){
                throw new IllegalStateException("Service was null");
            }
            try {
                departmentService.delete(obj);
                updateTableView();
            }
            catch (DbException e){
                Alerts.showAlert("Error removing object", null, e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    //Cada departamento vai ter um botão para editar
    private void initEditButtons(){
        tableColumnEdit.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
        tableColumnEdit.setCellFactory(param -> new TableCell<Department, Department>() {
            private final Button button = new Button("edit");
            @Override
            protected void updateItem(Department obj, boolean empty) {
                super.updateItem(obj, empty);
                if (obj == null) {
                    setGraphic(null);
                    return;
                }
                setGraphic(button);
                button.setOnAction(
                        event -> createDialogForm(
                                obj, "DepartmentForms.fxml",Utils.currentStage(event)));
            }
        });
    }

    //Quando notificar que os dados foram alterados vamos atualizar a tabela
    @Override
    public void onDataChanged() {
        updateTableView();
    }

    //-----------------------------------------------------------------------//

    //  INICIALIZAÇÃO
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeNode();

    }
    private void initializeNode(){
        //Inicia comportamento das colunas
        tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
        tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));
        //Faz a table view ficar até o final da tela
        Stage stage = (Stage) Application.getMainScene().getWindow(); //Pega referencia da janela que é uma superclasse do stage
        tableViewDepartment.prefHeightProperty().bind(stage.heightProperty()); //Comando para a table view acompanhar o tamanho da janela
    }

    //-----------------------------------------------------------------//

    //  MAIN LISTA DE DEPARTAMENTO

    public void updateTableView(){
        if(departmentService==null) {
            throw new IllegalStateException("Service is null");
        }
        observableList = FXCollections.observableArrayList();
        observableList.addAll(departmentService.findAll());
        tableViewDepartment.setItems(observableList);
        initEditButtons(); //Vai colocar um botão a cada linha para editar
        initDeleteButton();
    }

    private void createDialogForm(Department department, String absoluteName, Stage parentStage){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
            AnchorPane pane = loader.load();

            DepartmentFormsController controller = loader.getController();
            controller.setDepartment(department);
            controller.setDepartmentService(new DepartmentService());
            controller.subscriteDataChangeListener(this); //Se inscreve para receber o evento de inscrição e então executa o onDataChangeListener
            controller.updateFormData();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Enter department data");
            dialogStage.setScene(new Scene(pane));
            dialogStage.setResizable(false); //Diz se a janela pode ou não ser redimensionada;
            dialogStage.initOwner(parentStage); //Stage pai da janela
            dialogStage.initModality(Modality.WINDOW_MODAL); //diz se a janela é modal ou outro comportamento, modal é travado enqanto não fecha ela

            dialogStage.showAndWait();


        }
        catch (IOException e){
            //  Alerts.showAlert("Io Exception", "Error loading view", e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    //---------------------------------------------------------------//

    //  GETTERS AND SETTERS
    public void setDepartmentService(DepartmentService departmentService){
        this.departmentService = departmentService;
    }


}
