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

    //BOTÕES

    //1. É executado assim que clica no botão seller no menu principa
    @FXML
    public void onButtonRegistrerAction(ActionEvent event){ //pega a referência do controller que realizou o event
        Stage parentStage = Utils.currentStage(event); //pega o estágio do menu
        Seller Seller = new Seller(); //instancia o vendedor
        //Abre o dialog form, que é a tela com a lista do vendedores, passa o vendedor, o arquivo FXML e o estágio pai do menu
        createDialogForm(Seller, "SellerForms.fxml", parentStage);
    }

    //Quando notificar que os dados foram alterados vamos atualizar a tabela
    @Override
    public void onDataChanged() {
        updateTableView();
    }

    //Cria o botão de delete para cada item na lista
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

    //Ação do botão de deletar
    private void removeEntity(Seller obj){
        //Botão de opção do Alert
        Optional<ButtonType> result = Alerts.showConfirmation("Confirmation", "Are you sure to delete?");
        //Se o botão for clicado
        if (result.get() == ButtonType.OK){
            if (SellerService == null){ //Programação defensiva para caso o programador esqueça de instanciar o Service
                throw new IllegalStateException("Service was null");
            }
            try {
                //Realiza a deleção e atualiza a tela
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
                //Se clicar no botão abre a tela de formulário
                button.setOnAction(
                        event -> createDialogForm(
                                obj, "SellerForms.fxml",Utils.currentStage(event)));
            }
        });
    }


    // ---------------------------------------------------------------//

    //  INICIALIZAÇÃO

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //Inicia o nó
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


    // ----------------------------------------------------------------------//

    //  Main do SellerList
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
            //Carrega a AnchorPane
            AnchorPane pane = loader.load();
            //Pega o controlador do formulário
            SellerFormsController controller = loader.getController();
            //"Seta" o vendedor que foi instanciado no onButtonRegister
            controller.setSeller(Seller);
            //"Seta" o serviço do Seller e do Department para o departamento que está junto ao usuário
            controller.setServices(new SellerService(), new DepartmentService());
            //Carrega os objetos associados que é meu comboBox de departamento
            controller.loadAssociateObjects();
            //Se inscreve para receber o evento de inscrição e então executa o onDataChangeListener
            controller.subscriteDataChangeListener(this);
            //Atualiza a lista que está aparecendo na tela
            controller.updateFormData();
            //Cria um novo Stage que é o de formulário
            Stage dialogStage = new Stage();
            //"Seta" o título
            dialogStage.setTitle("Enter Seller data");
            //"Seta" a cena que é o AnchorPane
            dialogStage.setScene(new Scene(pane));
            //Diz se a janela pode ou não ser redimensionada;
            dialogStage.setResizable(false);
            //Stage pai da janela
            dialogStage.initOwner(parentStage);
            //diz se a janela é modal ou outro comportamento, modal é travado enqanto não fecha ela
            dialogStage.initModality(Modality.WINDOW_MODAL);

            dialogStage.showAndWait();
        }
        catch (IOException e){
            Alerts.showAlert("Io Exception", "Error loading view", e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    //-------------------------------------------------------------------------//

    // SETTERS

    public void setSellerService(SellerService SellerService){
        this.SellerService = SellerService;
    }
}
