package com.example.javafxjdbcmvc.gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;

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
    public void onMenuItemSellerAction() {
        System.out.println("Seller");

    }
    @FXML
    public void onMenuItemDepartmentAction() {
        System.out.println("Department");
    }

    @FXML
    public void onAboutAction() {
        System.out.println("About");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }
}