package controllers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import dtos.DtoResponsePreview;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import okhttp3.*;
import org.example.Main;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class DetailsController implements Initializable {

    private Gson gson;
    private OkHttpClient client;
    private List<DtoResponsePreview> responsePreviewList;
    private Thread fetchPreviewWorlds;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.fetchPreviewWorlds = new Thread(this::fetchPreviewSimulations);
        this.gson = new Gson();
        this.client = Main.getClient();
    }
    @FXML
    private void SelectedItem(){

    }

    public DtoResponsePreview fetchWorldPreviewBySimulationName(String simulationName) {
        DtoResponsePreview dtoResponsePreview = null;
        for (DtoResponsePreview responsePreview: this.responsePreviewList) {
            if (responsePreview.getSimulationName().equalsIgnoreCase(simulationName)){
                dtoResponsePreview = responsePreview;
            }
        }
        return dtoResponsePreview;
    }

    private void fetchPreviewSimulations() {
        while (true) {
            Request simulationFetchRequest = new Request.Builder().url(Main.getBaseUrl() + "/admin/previewWorlds").build();
            Call call = this.client.newCall(simulationFetchRequest);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    List<DtoResponsePreview> responsePreviewList = gson.fromJson(response.body().string(), new TypeToken<List<DtoResponsePreview>>() {
                    }.getType());
                    if (CheckIfAddNewSimulation(responsePreviewList)){
                        //TODO UPDATE THE TREEVIEW
                    }
                }
            });
            try{
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private boolean CheckIfAddNewSimulation(List<DtoResponsePreview> responsePreviewList){
        List<DtoResponsePreview> dtoResponsePreviews = new ArrayList<>();
        boolean copyExist = false;
        boolean foundNewWorld = false;
        for (DtoResponsePreview responsePreview : responsePreviewList) {
            copyExist = false;
            dtoResponsePreviews.add(responsePreview);
            for (DtoResponsePreview dtoResponsePreview : this.responsePreviewList) {
                if (dtoResponsePreview.getSimulationName().equalsIgnoreCase(responsePreview.getSimulationName())){
                    copyExist = true;
                    foundNewWorld = true;
                }
            }
            if (!copyExist){
                dtoResponsePreviews.add(responsePreview);
            }
        }
        this.responsePreviewList = dtoResponsePreviews;
        return foundNewWorld;
    }
}
