package com.example.javafxjdbcmvc.gui;

import com.example.javafxjdbcmvc.Application;
import com.example.javafxjdbcmvc.gui.listeners.DataChangeListener;
import com.example.javafxjdbcmvc.gui.util.Utils;
import com.example.javafxjdbcmvc.model.entities.Department;
import com.example.javafxjdbcmvc.model.services.DepartmentService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class DepartmentListController implements Initializable, DataChangeListener {
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
    @FXML
    public void onButtonRegistrerAction(ActionEvent event){ //pega a referência do controller que realizou o event
        Stage parentStage = Utils.currentStage(event);
        Department department = new Department();
        createDialogForm(department, "DepartmentForms.fxml", parentStage);
    }



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
    public void setDepartmentService(DepartmentService departmentService){
        this.departmentService = departmentService;
    }

    public void updateTableView(){
        if(departmentService==null) {
            throw new IllegalStateException("Service is null");
        }
        observableList = FXCollections.observableArrayList();
        observableList.addAll(departmentService.findAll());
        tableViewDepartment.setItems(observableList);
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

    //Quando notificar que os dados foram alterados vamos atualizar a tabela
    @Override
    public void onDataChanged() {
        updateTableView();
    }
}
