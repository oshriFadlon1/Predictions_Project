package presenters;

public class EntityPresenter {
    private String entityName;
    private int startPopulation;
    private int currPopulation;
    public EntityPresenter(String entityName, int startPopulation) {
        this.entityName = entityName;
        this.startPopulation = startPopulation;
    }

    public EntityPresenter(String entityName, int startPopulation, int currPopulation) {
        this.entityName = entityName;
        this.startPopulation = startPopulation;
        this.currPopulation = currPopulation;
    }

    public String getEntityName() {
        return entityName;
    }
    public int getStartPopulation() {
        return startPopulation;
    }
    public int getEndPopulation(){
        return currPopulation;
    }

    public void setEndPopulation(int endPopulation) {
        this.currPopulation = endPopulation;
    }
}
