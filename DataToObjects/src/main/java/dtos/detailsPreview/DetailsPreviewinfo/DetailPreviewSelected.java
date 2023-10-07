package dtos.detailsPreview.DetailsPreviewinfo;

import dtos.DtoResponsePreview;

public class DetailPreviewSelected {
    private DtoResponsePreview selectedDtoResponsePreview;
    private int countLevels;

    public DetailPreviewSelected(DtoResponsePreview dtoResponsePreview, int countLevels) {
        this.selectedDtoResponsePreview = dtoResponsePreview;
        this.countLevels = countLevels;
    }

    public DtoResponsePreview getSelectedDtoResponsePreview() {
        return selectedDtoResponsePreview;
    }

    public void setSelectedDtoResponsePreview(DtoResponsePreview selectedDtoResponsePreview) {
        this.selectedDtoResponsePreview = selectedDtoResponsePreview;
    }

    public int getCountLevels() {
        return countLevels;
    }

    public void setCountLevels(int countLevels) {
        this.countLevels = countLevels;
    }
}
