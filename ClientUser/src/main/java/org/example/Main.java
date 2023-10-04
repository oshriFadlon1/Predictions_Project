package org.example;

import controllers.LoginController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import okhttp3.OkHttpClient;

import java.net.URL;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main extends Application {
    private static String USER_NAME;
    private static final String BASE_URL = "http://localhost:8080";
    private static final OkHttpClient CLIENT = new OkHttpClient();
    public static void main(String[] args) {
        launch(args);
    }

    public static void setUserName(String userName) {
        USER_NAME = userName;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        URL mainFXML = getClass().getResource("/scenes/loginpagebasic.fxml");
        loader.setLocation(mainFXML);
        AnchorPane root = loader.load();
        LoginController loginController = loader.getController();

        loginController.setPrimaryStage(primaryStage);

        primaryStage.setTitle("Prediction");
        Scene scene = new Scene(root, 1100, 700);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static String getUserName() {
        return USER_NAME;
    }
    public static OkHttpClient getClient(){
        return CLIENT;
    }
    public static String getBaseUrl(){
        return BASE_URL;
    }
}