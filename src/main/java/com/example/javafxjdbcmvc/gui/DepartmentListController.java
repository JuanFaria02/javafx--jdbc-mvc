package com.example.javafxjdbcmvc.gui;

import com.example.javafxjdbcmvc.Application;
import com.example.javafxjdbcmvc.model.entities.Department;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.net.URL;
import java.util.ResourceBundle;

public class DepartmentListController implements Initializable {
    @FXML
    private TableView<Department> tableViewDepartment;
    @FXML
    private TableColumn<Department, Integer> tableColumnId;
    @FXML
    private TableColumn<Department, String> tableColumnName;

    @FXML
    private Button buttonRegistrer;
    @FXML
    public void onButtonRegistrerAction(){
        System.out.println("Buttom click");
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
}
