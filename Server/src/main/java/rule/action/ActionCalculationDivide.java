package rule.action;

import dto.DtoActionResponse;
import entity.EntityDefinition;
import enums.Operation;
import exceptions.GeneralException;
import necessaryVariables.NecessaryVariablesImpl;
import property.PropertyInstance;

import java.io.Serializable;

public class ActionCalculationDivide extends ActionCalculation implements Serializable {
    private String arg1;
    private String arg2;

    public ActionCalculationDivide(EntityDefinition entityDefinition, String propertyPlacement, String arg1, String arg2) {
        super(entityDefinition, propertyPlacement);
        this.arg1 = arg1;
        this.arg2 = arg2;
    }

    public String getArg1() {
        return arg1;
    }

    public void setArg1(String arg1) {
        this.arg1 = arg1;
    }

    public String getArg2() {
        return arg2;
    }

    public void setArg2(String arg2) {
        this.arg2 = arg2;
    }


    @Override
    public void invoke(NecessaryVariablesImpl context) throws GeneralException {
        if (!context.getPrimaryEntityInstance().getDefinitionOfEntity().getEntityName().equalsIgnoreCase(super.getEntityDefinition().getEntityName())){
            return;
        }

        Object y = null,x = null,result = null;
        try {
            x = context.getValueFromString(this.arg1);
            y = context.getValueFromString(this.arg2);
        } catch (GeneralException e) {
            throw e;
        }
        float numericValue = ((Number) y).floatValue();
        if (numericValue == 0.0F) {
            throw new GeneralException("Divide action can't operate on divide 0");
        }

        result = ((Number)x).floatValue() / ((Number)y).floatValue();

        // updating result on the property
        PropertyInstance propertyInstance = context.getPrimaryEntityInstance().getPropertyByName(super.getPropertyPlacement());
        propertyInstance.updatePropertyValue(result);
    }


    @Override
    public Operation getOperationType() {
        return Operation.CALCULATION;
    }

    @Override
    public EntityDefinition getContextEntity() {
        return super.getEntityDefinition();
    }

    @Override
    public DtoActionResponse getActionResponse() {
        DtoActionResponse actionResponse = super.getActionResponse();
        actionResponse.setActionName("Calculation Divide");
        actionResponse.setActionValue(this.arg1);
        actionResponse.setArg2(this.arg2);
        return actionResponse;
    }
}
