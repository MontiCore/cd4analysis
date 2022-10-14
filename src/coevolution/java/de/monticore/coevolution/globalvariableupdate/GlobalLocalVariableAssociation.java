package de.monticore.coevolution.globalvariableupdate;

import de.monticore.coevolution.methodupdate.MethodParameterTypeRefactoring;
import de.monticore.coevolution.methodupdate.MethodReturnTypRefactoring;
import spoon.reflect.CtModel;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.reference.CtParameterReference;
import spoon.reflect.visitor.filter.AbstractFilter;

import java.util.Collections;
import java.util.List;

public class GlobalLocalVariableAssociation {

  private final CtVariable<?> variableToBeUpdated;
  private final CtClass<?> targetClass;
  private final String targetVariable;
  private final String targetVariableType;
  private final CtModel model;

  public GlobalLocalVariableAssociation(CtVariable<?> variableToBeUpdated, CtClass<?> targetClass, String targetVariable,
                                        String targetVariableType, CtModel model){
    this.variableToBeUpdated = variableToBeUpdated;
    this.targetClass = targetClass;
    this.targetVariable = targetVariable;
    this.targetVariableType = targetVariableType;
    this.model = model;
  }

  public void connectGlobalLocalVariables(){

    //find the local parameter associated with the global variable.
    List<CtParameterReference<?>> parameterList = targetClass.getElements(new AbstractFilter<CtParameterReference<?>>() {
      @Override
      public boolean matches(CtParameterReference element) {
        return element.getParent(CtStatement.class).toString().matches
          ("(this)\\s*(.)\\s*" + targetVariable + "\\s*(=)\\s*" + element.getSimpleName());
      }
    });

    if (!parameterList.isEmpty()) {
      CtParameter<?> parameter = targetClass.getElements(new AbstractFilter<CtParameter<?>>() {
        @Override
        public boolean matches(CtParameter<?> element) {
          return element.getSimpleName().equals(parameterList.get(0).getSimpleName()) &&
            element.getParent(CtMethod.class).equals(parameterList.get(0).getParent(CtMethod.class));
        }
      }).get(0);

      // update the corresponding local variable.
      MethodParameterTypeRefactoring rtrm = new MethodParameterTypeRefactoring(Collections.singleton(model.getRootPackage()), targetClass);
      rtrm.setTarget(parameter).setNewName(targetVariableType).refactor();
    }
    //if the local variable appears in the return statements, update the corresponding method return type.
    List<CtVariableAccess<?>> variablesTypeOfReturn = targetClass.getElements(new AbstractFilter<>(CtVariableAccess.class) {
      @Override
      public boolean matches(CtVariableAccess variable) {
        return variable.getParent().toString().contains("return") &&
          variable.getVariable().getSimpleName().equals(targetVariable);
      }
    });

    if (variablesTypeOfReturn.size() != 0) {
      for(CtVariableAccess<?> variable: variablesTypeOfReturn){
        CtMethod<?> method = variable.getParent(CtMethod.class);
        if (!method.getType().equals(variableToBeUpdated.getType())) {
          MethodReturnTypRefactoring mrtr = new MethodReturnTypRefactoring(Collections.singleton(model.getRootPackage()),targetClass);
          mrtr.setTarget(method).setNewName(variableToBeUpdated.getType().getSimpleName()).refactor();
        }
      }
    }










  }
}
