package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import okhttp3.*;
import okio.Timeout;
import org.example.Main;
import org.jetbrains.annotations.NotNull;

public class LoginController implements Initializable {
    private Stage primaryStage;
    private Scene scene;
    @FXML
    private Label labelError;
    @FXML
    private TextField textFIeldUserName;
    private OkHttpClient client;

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    @FXML
    public void onClickLogin() throws IOException {
        if(this.textFIeldUserName.getText().equalsIgnoreCase("")){
            printErrorMsg("Please fill in your username");
            return;
        }

        Request loginRequest = new Request.Builder().url(Main.getBaseUrl() + "/user/login?userName=" + this.textFIeldUserName.getText()).build();
        Call call = this.client.newCall(loginRequest);
        try{
            final Response response = call.execute();
            if(response.body().string().equalsIgnoreCase("false")){
                printErrorMsg("Username " + this.textFIeldUserName.getText() + " is already logged in");
            }
            else{
               Main.setUserName(this.textFIeldUserName.getText());
                switchToUserScene();
            }
        }
        catch(IOException e){
            printErrorMsg("There was a probelm connecting");
        }
    }

    private void printErrorMsg(String errorMsg) {
        this.labelError.setVisible(true);
        this.labelError.setText(errorMsg);
    }

    public void switchToUserScene()throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/scenes/usermenu.fxml"));
        Scene scene2 = new Scene(root, 1200, 800);
        this.primaryStage.setScene(scene2);
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.client = Main.getClient();
    }
}
