package entity;

public class EntityToPopulation {
    private EntityDefinition currEntityDef;
    private int currEntityPopulation;
    private int startEntityPopulation;

    public EntityToPopulation(EntityDefinition currEntityDef, int currEntityPopulation, int startEntityPopulation) {
        this.currEntityDef = currEntityDef;
        this.currEntityPopulation = currEntityPopulation;
        this.startEntityPopulation = startEntityPopulation;
    }

    public EntityDefinition getCurrEntityDef() {
        return currEntityDef;
    }

    public void setCurrEntityDef(EntityDefinition currEntityDef) {
        this.currEntityDef = currEntityDef;
    }

    public int getCurrEntityPopulation() {
        return currEntityPopulation;
    }

    public void setCurrEntityPopulation(int currEntityPopulation) {
        this.currEntityPopulation = currEntityPopulation;
    }

    public int getStartEntityPopulation() {
        return startEntityPopulation;
    }

    public void setStartEntityPopulation(int startEntityPopulation) {
        this.startEntityPopulation = startEntityPopulation;
    }

    @Override
    public String toString() {
        return "EntityToPopulation{" +
                "currEntityDef=" + currEntityDef +
                ", currEntityPopulation=" + currEntityPopulation +
                ", startEntityPopulation=" + startEntityPopulation +
                '}';
    }
}
