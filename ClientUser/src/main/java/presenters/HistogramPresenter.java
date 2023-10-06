package presenters;

public class HistogramPresenter {
    private String propertyValue;
    private Integer countOfProperty;
    public HistogramPresenter(String propertyValue, Integer countOfProperty) {
        this.propertyValue = propertyValue;
        this.countOfProperty = countOfProperty;
    }

    public String getPropertyValue() {
        return propertyValue;
    }
    public Integer getCountOfProperty() {
        return countOfProperty;
    }
}
