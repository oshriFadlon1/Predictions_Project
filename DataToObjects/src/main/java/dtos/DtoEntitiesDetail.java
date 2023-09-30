package dtos;

import java.util.List;

public class DtoEntitiesDetail {
    private String entityName;
    private List<DtoPropertyDetail> propertyDefinitionEntityList;

    public DtoEntitiesDetail(String entityName, List<DtoPropertyDetail> propertyDefinitionEntityList) {
        this.entityName = entityName;
        this.propertyDefinitionEntityList = propertyDefinitionEntityList;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public List<DtoPropertyDetail> getPropertyDefinitionEntityList() {
        return propertyDefinitionEntityList;
    }

    public void setPropertyDefinitionEntityList(List<DtoPropertyDetail> propertyDefinitionEntityList) {
        this.propertyDefinitionEntityList = propertyDefinitionEntityList;
    }
}
