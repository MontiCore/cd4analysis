package de.monticore.coevolution;

import spoon.processing.AbstractProcessor;
import spoon.reflect.code.*;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtTypeReference;

public class TypeUpdater extends AbstractProcessor<CtElement> {
  String variableToBeChanged;
  String newType;

  public TypeUpdater(String variable, String newtype){
    this.variableToBeChanged = variable;
    this.newType = newtype;
  }

  @Override
  public boolean isToBeProcessed(CtElement candidate) {

    if (!(candidate instanceof CtVariableAccess) && !(candidate instanceof CtVariable)) {
      return false;
    }

    if(candidate instanceof CtVariableAccess){
      CtVariableAccess can = (CtVariableAccess) candidate;
      return can.getVariable().getSimpleName().equals(variableToBeChanged);
    }

    CtVariable can = (CtVariable) candidate;
    return can.getSimpleName().equals(variableToBeChanged);

  }

  @Override
  public void process(CtElement candidate) {

    final Factory factory = candidate.getFactory();
    CtTypeReference typeToBeSet = factory.createReference(newType);

    //CtCodeSnippetStatement snippet = factory.Core().createCodeSnippetStatement();
    CtCodeSnippetExpression snippet = factory.Core().createCodeSnippetExpression();

    final String value = String.format("Integer.parseInt(%s)", variableToBeChanged);
    snippet.setValue(value);

    if (!(candidate instanceof CtVariableAccess) && !(candidate instanceof CtVariable)) {
      return;
    }
    if(candidate instanceof CtVariableAccess){
      CtVariableAccess op = (CtVariableAccess) candidate;

      op.getVariable().setType(typeToBeSet);

      replace(op,snippet);
    }

    if(candidate instanceof CtVariable){
      CtVariable op = (CtVariable) candidate;
      op.setType(typeToBeSet);
    }
  }

  private void replace(CtElement e, CtElement op) {
    if (e instanceof CtStatement  && op instanceof CtStatement) {
      e.replace(op);
      return;
    }
    if (e instanceof CtExpression && op instanceof CtExpression) {
      e.replace(op);
      return;
    }
    throw new IllegalArgumentException(e.getClass()+" "+op.getClass());
  }
}
