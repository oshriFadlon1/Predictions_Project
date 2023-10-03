package presenters;

import javafx.scene.control.ListCell;

public class CustomCellItem extends ListCell<SimulationPresenter> {
    protected void updateItem(SimulationPresenter item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
            setText(null);
        } else {
            // Customize the text representation of the item
            String text = item.getSimulationId() + ") " + (item.getSimulationName());
            setText(text);
        }
    }
}
