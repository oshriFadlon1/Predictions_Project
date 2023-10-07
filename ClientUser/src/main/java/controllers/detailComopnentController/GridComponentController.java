package controllers.detailComopnentController;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class GridComponentController {

    @FXML
    private Label col;

    @FXML
    private Label row;


    public void updateLabelTerm(int row, int col){

        this.col.setText("Column: " + col);
        this.row.setText("Rows: " + row);

    }
}
