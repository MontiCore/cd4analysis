package de.monticore.coevolution;

import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtVariable;


public class NameUpdater extends AbstractProcessor<CtElement> {
  String nameToBeChanged;
  String newName;

  public NameUpdater(String oldname, String newname){
    this.nameToBeChanged = oldname;
    this.newName = newname;
  }

  @Override
  public boolean isToBeProcessed(CtElement candidate) {

    if (!(candidate instanceof CtVariableAccess) && !(candidate instanceof CtVariable)) {
      return false;
    }

    if(candidate instanceof CtVariableAccess){
      CtVariableAccess can = (CtVariableAccess) candidate;
      return can.getVariable().getSimpleName().equals(nameToBeChanged);
    }

    CtVariable can = (CtVariable) candidate;
    return can.getSimpleName().equals(nameToBeChanged);

  }

  @Override
  public void process(CtElement candidate) {
    if (!(candidate instanceof CtVariableAccess) && !(candidate instanceof CtVariable)) {
      return;
    }
    if(candidate instanceof CtVariableAccess){
      CtVariableAccess op = (CtVariableAccess) candidate;
      op.getVariable().setSimpleName(newName);
    }
    if(candidate instanceof CtVariable){
      CtVariable op = (CtVariable) candidate;
      op.setSimpleName(newName);
    }
  }
}
