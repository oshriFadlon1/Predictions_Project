package presenters;

public class EntityPresenter {
    private String entityName;
    private int population;
    public EntityPresenter(String entityName, int population) {
        this.entityName = entityName;
        this.population = population;
    }
    public String getEntityName() {
        return entityName;
    }
    public int getPopulation() {
        return population;
    }
}
