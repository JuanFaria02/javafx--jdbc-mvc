package com.example.javafxjdbcmvc.gui;

import com.example.javafxjdbcmvc.Application;
import com.example.javafxjdbcmvc.db.exceptions.DbException;
import com.example.javafxjdbcmvc.gui.listeners.DataChangeListener;
import com.example.javafxjdbcmvc.gui.util.Alerts;
import com.example.javafxjdbcmvc.gui.util.Utils;
import com.example.javafxjdbcmvc.model.entities.Seller;
import com.example.javafxjdbcmvc.model.services.DepartmentService;
import com.example.javafxjdbcmvc.model.services.SellerService;
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
import java.time.LocalDate;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

public class SellerListController implements Initializable, DataChangeListener {
    @FXML
    private TableColumn<Seller, Seller> tableColumnDelete;
    @FXML
    private TableColumn<Seller, Seller> tableColumnEdit; //Table para edição
    @FXML
    private ObservableList<Seller> observableList;
    private SellerService SellerService;
    @FXML
    private TableView<Seller> tableViewSeller;
    @FXML
    private TableColumn<Seller, Integer> tableColumnId;
    @FXML
    private TableColumn<Seller, String> tableColumnName;
    @FXML
    private TableColumn<Seller, LocalDate> tableColumnBirthDate;
    @FXML
    private TableColumn<Seller, Double> tableColumnBaseSalary;
    @FXML
    private TableColumn<Seller, String> tableColumnEmail;

    @FXML
    private Button buttonRegistrer;

    @FXML
    public void onButtonRegistrerAction(ActionEvent event){ //pega a referência do controller que realizou o event
        Stage parentStage = Utils.currentStage(event);
        Seller Seller = new Seller();
        createDialogForm(Seller, "SellerForms.fxml", parentStage);
    }

    private void initDeleteButton(){
        tableColumnDelete.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
        tableColumnDelete.setCellFactory(param -> new TableCell<Seller, Seller>() {
            private final Button button = new Button("remove");
            @Override
            protected void updateItem(Seller obj, boolean empty) {
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

    private void removeEntity(Seller obj){
        Optional<ButtonType> result = Alerts.showConfirmation("Confirmation", "Are you sure to delete?");
        if (result.get() == ButtonType.OK){
            if (SellerService == null){
                throw new IllegalStateException("Service was null");
            }
            try {
                SellerService.delete(obj);
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
        tableColumnEdit.setCellFactory(param -> new TableCell<Seller, Seller>() {
            private final Button button = new Button("edit");
            @Override
            protected void updateItem(Seller obj, boolean empty) {
                super.updateItem(obj, empty);
                if (obj == null) {
                    setGraphic(null);
                    return;
                }
                setGraphic(button);
                button.setOnAction(
                        event -> createDialogForm(
                                obj, "SellerForms.fxml",Utils.currentStage(event)));
            }
        });
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeNode();

    }
    private void initializeNode(){
        //Inicia comportamento das colunas, o nome passado é o do atributo da classe
        tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
        tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));
        tableColumnEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        tableColumnBaseSalary.setCellValueFactory(new PropertyValueFactory<>("baseSalary"));
        tableColumnBirthDate.setCellValueFactory(new PropertyValueFactory<>("birthDate"));

        //Formatação
        Utils.formatTableColumnDate(tableColumnBirthDate, "dd/MM/yyyy");
        Utils.formatTableColumnDouble(tableColumnBaseSalary, 2);
        //Faz a table view ficar até o final da tela
        Stage stage = (Stage) Application.getMainScene().getWindow(); //Pega referencia da janela que é uma superclasse do stage
        tableViewSeller.prefHeightProperty().bind(stage.heightProperty()); //Comando para a table view acompanhar o tamanho da janela
    }
    public void setSellerService(SellerService SellerService){
        this.SellerService = SellerService;
    }

    public void updateTableView(){
        if(SellerService==null) {
            throw new IllegalStateException("Service is null");
        }
        observableList = FXCollections.observableArrayList();
        observableList.addAll(SellerService.findAll());
        tableViewSeller.setItems(observableList);
        initEditButtons(); //Vai colocar um botão a cada linha para editar
        initDeleteButton();
    }

    private void createDialogForm(Seller Seller, String absoluteName, Stage parentStage){

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
            AnchorPane pane = loader.load();

            SellerFormsController controller = loader.getController();
            controller.setSeller(Seller);
            controller.setServices(new SellerService(), new DepartmentService());
            controller.loadAssociateObjects();
            controller.subscriteDataChangeListener(this); //Se inscreve para receber o evento de inscrição e então executa o onDataChangeListener
            controller.updateFormData();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Enter Seller data");
            dialogStage.setScene(new Scene(pane));
            dialogStage.setResizable(false); //Diz se a janela pode ou não ser redimensionada;
            dialogStage.initOwner(parentStage); //Stage pai da janela
            dialogStage.initModality(Modality.WINDOW_MODAL); //diz se a janela é modal ou outro comportamento, modal é travado enqanto não fecha ela

            dialogStage.showAndWait();


        }
        catch (IOException e){
            Alerts.showAlert("Io Exception", "Error loading view", e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }



    //Quando notificar que os dados foram alterados vamos atualizar a tabela
    @Override
    public void onDataChanged() {
        updateTableView();
    }
}
