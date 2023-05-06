package com.example.javafxjdbcmvc;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.stage.Stage;

import java.io.IOException;

public class Application extends javafx.application.Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("MainView.fxml"));
        ScrollPane scrollPane = fxmlLoader.load();//Usado para instanciar o objeto correto que será usado
        scrollPane.setFitToHeight(true);//Feito para deixar padrão a altura
        scrollPane.setFitToWidth(true); //Feito para deixar padrão a largura 
        Scene scene = new Scene(scrollPane);
        stage.setTitle("Sample JavaFx Application");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}