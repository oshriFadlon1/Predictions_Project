package presenters;

public class EnvironmentPresenter {
    private String envName;
    private Object envValue;

    public EnvironmentPresenter(String envName, Object envValue) {
        this.envName = envName;
        this.envValue = envValue;
    }

    public String getEnvName() {
        return envName;
    }

    public Object getEnvValue() {
        return envValue;
    }
}
