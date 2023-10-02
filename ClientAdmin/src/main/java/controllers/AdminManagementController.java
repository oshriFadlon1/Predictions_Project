package controllers;

import dtos.DtoQueueManagerInfo;
import dtos.DtoResponsePreview;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import okhttp3.*;
import org.example.Main;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.jetbrains.annotations.NotNull;

public class AdminManagementController implements Initializable {
    private OkHttpClient client;
    private Thread queueDataFetchingThread;
    @FXML
    private Button buttonLoadFile;
    @FXML
    private Button buttonThreadCount;
    @FXML
    private TextField textFieldThreadCount;
    @FXML
    private ScrollPane rootScrollPane;
    @FXML
    private Label labelThreadCountMsg;
    @FXML
    private Label labelLoadStatus;
    @FXML
    private Label labelSimulationsEnded;
    @FXML
    private Label labelSimulationsPending;
    @FXML
    private Label labelSimulationsProgress;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.queueDataFetchingThread = new Thread(this::fetchQueueData);
        this.queueDataFetchingThread.start();
    }

    @FXML
    private void onLoadButton(){
        Stage stage = (Stage)rootScrollPane.getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter xmlFilter = new FileChooser.ExtensionFilter("XML Files", "*.xml");
        fileChooser.getExtensionFilters().add(xmlFilter);
        File file =  fileChooser.showOpenDialog(stage);

           //TODO later need to add simulations to the treeview

        OkHttpClient client = new OkHttpClient();

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", file.getName(), RequestBody.create(MediaType.parse("application/octet-stream"), file))
                .build();

        String url = "http://localhost:8080/admin/upload";
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected response code: " + response);
            }

            // Handle the response here
            String responseBody = response.body().string();
            System.out.println("Response: " + responseBody);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @FXML
    private void onButtonThreadCount(){
        try{
            String numberOfThreadAsString = this.textFieldThreadCount.getText();

            MediaType mediaType = MediaType.parse("text/plain");
            RequestBody requestBody = RequestBody.create(mediaType, numberOfThreadAsString);
            Request threadCountRequest = new Request.Builder().url(Main.getBaseUrl() + "/admin/setThreadCount").patch(requestBody).build();

            Call call = this.client.newCall(threadCountRequest);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    Platform.runLater(() -> {
                        labelThreadCountMsg.setText("Updated thread count");
                        labelThreadCountMsg.setVisible(true);
                    });
                }
            });
        }
        catch (NumberFormatException e){
            this.labelThreadCountMsg.setVisible(true);
            this.labelThreadCountMsg.setText("Invalid thread count value");
        }

    }

    private void fetchQueueData(){
        if(this.client == null){
            this.client = Main.getCLIENT();
        }
        while(true){
        Request queueRequest = new Request.Builder().url(Main.getBaseUrl() + "/admin/getQueue").build();
        Call call = this.client.newCall(queueRequest);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Gson gson = new Gson();
                DtoQueueManagerInfo queueManagerInfo = gson.fromJson(response.body().string(), DtoQueueManagerInfo.class);
                if (queueManagerInfo != null){
                    Platform.runLater(()->{
                        labelSimulationsEnded.setText(queueManagerInfo.getCountOfSimulationEnded());
                        labelSimulationsPending.setText(queueManagerInfo.getCountOfSimulationsPending());
                        labelSimulationsProgress.setText(queueManagerInfo.getCountOfSimulationInProgress());
                    });
                }
            }
        });
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
