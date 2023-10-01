package presenters;

public class EntityPresenter {
    private String entityName;
    private int startPopulation;
    private int endPopulation;

    public EntityPresenter(String entityName, int startPopulation, int endPopulation) {
        this.entityName = entityName;
        this.startPopulation = startPopulation;
        this.endPopulation = endPopulation;
    }

    public String getEntityName() {
        return entityName;
    }

    public int getStartPopulation() {
        return startPopulation;
    }

    public int getEndPopulation() {
        return endPopulation;
    }
}
