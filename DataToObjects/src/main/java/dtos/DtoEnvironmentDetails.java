package dtos;

public class DtoEnvironmentDetails {
    private String propertyName;

    private String propertyType;

    private float from;

    private float to;

    public DtoEnvironmentDetails(String propertyName, String propertyType, float from, float to) {
        this.propertyName = propertyName;
        this.propertyType = propertyType;
        this.from = from;
        this.to = to;
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

    @Override
    public String toString() {
        return "DtoEnvironmentDetails{" +
                "propertyName='" + propertyName + '\'' +
                ", propertyType='" + propertyType + '\'' +
                ", from=" + from +
                ", to=" + to +
                '}';
    }
}
