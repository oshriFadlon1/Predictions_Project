package dtos;

public class DtoPropertyDetail {
    private String propertyName;

    private String propertyType;

    private float from;

    private float to;

    private Boolean randomInit;

    private String init;

    public DtoPropertyDetail(String propertyName, String propertyType, float from, float to, Boolean randomInit, String init) {
        this.propertyName = propertyName;
        this.propertyType = propertyType;
        this.from = from;
        this.to = to;
        this.randomInit = randomInit;
        this.init = init;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public String getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(String propertyType) {
        this.propertyType = propertyType;
    }

    public float getFrom() {
        return from;
    }

    public void setFrom(float from) {
        this.from = from;
    }

    public float getTo() {
        return to;
    }

    public void setTo(float to) {
        this.to = to;
    }

    public Boolean getRandomInit() {
        return randomInit;
    }

    public void setRandomInit(Boolean randomInit) {
        this.randomInit = randomInit;
    }

    public String getInit() {
        return init;
    }

    public void setInit(String init) {
        this.init = init;
    }
}
