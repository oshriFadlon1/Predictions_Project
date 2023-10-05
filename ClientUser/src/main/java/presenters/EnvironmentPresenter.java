package presenters;

import range.Range;

public class EnvironmentPresenter {
    private String environmentName;
    private Object environmentVal;
    private Range environmentRange;
    private String environmentType;

    public EnvironmentPresenter(String environmentName, Object environmentVal) {
        this.environmentName = environmentName;
        this.environmentVal = environmentVal;
    }

    public EnvironmentPresenter(String environmentName, Range environmentRange, String environmentType) {
        this.environmentName = environmentName;
        this.environmentRange = environmentRange;
        this.environmentType = environmentType;
    }

    public EnvironmentPresenter(String environmentName, Range environmentRange, String environmentType, String remark) {
        this.environmentName = environmentName;
        this.environmentVal = environmentVal;
        this.environmentRange = environmentRange;
        this.environmentType = environmentType;
    }

    public Range getEnvironmentRange() {
        return environmentRange;
    }
    public String getEnvironmentType() {
        return environmentType;
    }public String getEnvironmentName() {
        return environmentName;
    } public Object getEnvironmentVal() {
        return environmentVal;
    }
}
