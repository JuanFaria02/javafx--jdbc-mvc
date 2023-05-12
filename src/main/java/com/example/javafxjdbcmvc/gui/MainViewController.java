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


    @FXML
    public void onMenuItemSellerAction() {
        //Inicializa o controlador como paramentro
        loadView("SellerList.fxml", (SellerListController controller)->{
            controller.setSellerService(new SellerService());
            controller.updateTableView();
        });

    }


    @FXML
    public void onMenuItemDepartmentAction() {
        //Inicializa o controlador como paramentro
        loadView("DepartmentList.fxml", (DepartmentListController controller)->{
            controller.setDepartmentService(new DepartmentService());
            controller.updateTableView();
        });
    }

    @FXML
    public void onAboutAction() {
        loadView("About.fxml", x->{});

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    //Função para abrir outra tela, passa o nome do arquivo xml e o controlador desejado
    private synchronized <T> void loadView(String absoluteName, Consumer<T> initializingAction){ //passa o nome da view
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
            VBox vBox = loader.load();
            Scene mainSceane = Application.getMainScene();
            VBox mainVbox = (VBox) ((ScrollPane)mainSceane.getRoot()).getContent(); //Acessa o conteudo do ScrollPane

            Node mainMenu = mainVbox.getChildren().get(0); //Pega o primeiro filho da janela principal
            mainVbox.getChildren().clear();

            mainVbox.getChildren().add(mainMenu);
            mainVbox.getChildren().addAll(vBox.getChildren());

            //Acessa o controlador
            T controller = loader.getController();
            initializingAction.accept(controller);

        }
        catch (IOException e){
            Alerts.showAlert("Io Exception","Error loading view", e.getMessage(), Alert.AlertType.ERROR);
        }
    }
}