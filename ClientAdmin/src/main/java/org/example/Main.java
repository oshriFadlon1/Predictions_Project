package org.example;

import controllers.AdminMenuController;
import dtos.DtoActionResponse;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.awt.*;
import java.io.IOException;
import java.net.URL;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main extends Application {
    private static OkHttpClient CLIENT = new OkHttpClient();
    private static String BASE_URL = "http://localhost:8080";

    public static void main(String[] args) {
//        Request loginRequest = new Request.Builder().url(BASE_URL + "/admin/login").build();
//        Call call = CLIENT.newCall(loginRequest);
//        try{
//            final Response response = call.execute();
//            if(response.body().string().equalsIgnoreCase("false")){
//                return;
//            }
//        }
//        catch(IOException e){
//            return;
//        }
        launch(args);
    }

    public static /*synchronized*/ OkHttpClient getCLIENT() {
        return CLIENT;
    }

    public static void setCLIENT(OkHttpClient CLIENT) {
        Main.CLIENT = CLIENT;
    }

    public static String getBaseUrl() {
        return BASE_URL;
    }

    public static void setBaseUrl(String baseUrl) {
        BASE_URL = baseUrl;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        FXMLLoader loader = new FXMLLoader();
        URL mainFXML = getClass().getResource("/scenes/adminmenu.fxml");
        loader.setLocation(mainFXML);
        ScrollPane root = loader.load();
        AdminMenuController adminMenuController = loader.getController();

        adminMenuController.setPrimaryStage(primaryStage);

        primaryStage.setTitle("Predictions");
        Scene scene = new Scene(root, 1200, 800);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}