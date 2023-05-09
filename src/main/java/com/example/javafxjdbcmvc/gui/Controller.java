package com.example.javafxjdbcmvc.gui;

import com.example.javafxjdbcmvc.Application;
import com.example.javafxjdbcmvc.gui.util.Alerts;
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


public class Controller implements Initializable {
    @FXML
    private Label welcomeText;
    @FXML
    private MenuItem menuItemSeller;

    @FXML
    private MenuItem getMenuItemDepartment;
    @FXML
    private MenuItem about;

    @FXML
    private Alerts alerts;

    @FXML
    public void onMenuItemSellerAction() {
        System.out.println("Seller");

    }
    @FXML
    public void onMenuItemDepartmentAction() {
        loadView("DepartmentList.fxml");
    }

    @FXML
    public void onAboutAction() {
        loadView("About.fxml");

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    //Função para abrir outra tela
    private synchronized void loadView(String absoluteName){ //passa o nome da view
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
            VBox vBox = loader.load();
            Scene mainSceane = Application.getMainScene();
            VBox mainVbox = (VBox) ((ScrollPane)mainSceane.getRoot()).getContent(); //Acessa o conteudo do ScrollPane

            Node mainMenu = mainVbox.getChildren().get(0); //Pega o primeiro filho da janela principal
            mainVbox.getChildren().clear();

            mainVbox.getChildren().add(mainMenu);
            mainVbox.getChildren().addAll(vBox.getChildren());
        }
        catch (IOException e){
            alerts.showAlert("Io Exception","Error loading view", e.getMessage(), Alert.AlertType.ERROR);
        }
    }
}