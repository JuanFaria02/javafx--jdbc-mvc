package com.example.javafxjdbcmvc.gui;

import com.example.javafxjdbcmvc.Application;
import com.example.javafxjdbcmvc.gui.util.Alerts;
import com.example.javafxjdbcmvc.model.services.DepartmentService;
import com.example.javafxjdbcmvc.model.services.SellerService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;


public class MainViewController implements Initializable {
    @FXML
    private Label welcomeText;
    @FXML
    private MenuItem menuItemSeller;

    @FXML
    private MenuItem getMenuItemDepartment;
    @FXML
    private MenuItem about;



    //BOTÕES

    //Inicia a tela de vendedor ao clicar no botão de Seller
    @FXML
    public void onMenuItemSellerAction() {
        //Chama o método que carrega a View do arquivo FXML passado, instancia o Service e atualiza a tabela de vendedores
        //Inicializa o controlador como paramentro
        loadView("SellerList.fxml", (SellerListController controller)->{
            controller.setSellerService(new SellerService());
            controller.updateTableView();
        });

    }

    //Inicia a tela de departamento ao clicar no botão de Department
    @FXML
    public void onMenuItemDepartmentAction() {
        //Chama o método que carrega a View do arquivo FXML passado, instancia o Service e atualiza a tabela de departamento
        //Inicializa o controlador como paramentro
        loadView("DepartmentList.fxml", (DepartmentListController controller)->{
            controller.setDepartmentService(new DepartmentService());
            controller.updateTableView();
        });
    }
    //Inicia a tela que diz informações sonbre o projeto
    @FXML
    public void onAboutAction() {
        loadView("About.fxml", x->{});

    }

    //--------------------------------------------------------------//


    //Inicialização


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }


    //Função para abrir outra tela, passa o nome do arquivo xml e o controlador desejado
    private synchronized <T> void loadView(String absoluteName, Consumer<T> initializingAction){ //passa o nome da view
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName)); //Armazena o arquivo passado
            VBox vBox = loader.load(); //Carrega o arquivo
            Scene mainSceane = Application.getMainScene(); //Pega a cena principal
            VBox mainVbox = (VBox) ((ScrollPane)mainSceane.getRoot()).getContent(); //Acessa o conteudo do ScrollPane

            Node mainMenu = mainVbox.getChildren().get(0); //Pega o primeiro filho da janela principal
            mainVbox.getChildren().clear(); //Limpa os filhos da janela principal e só deixa a base

            mainVbox.getChildren().add(mainMenu); //Adiciona como filho o primeiro filho da janela principal
            mainVbox.getChildren().addAll(vBox.getChildren());//Adiciona os outros componentes na tela

            //Acessa o controlador
            T controller = loader.getController();
            initializingAction.accept(controller);

        }
        catch (IOException e){
            Alerts.showAlert("Io Exception","Error loading view", e.getMessage(), Alert.AlertType.ERROR);
        }
    }
}