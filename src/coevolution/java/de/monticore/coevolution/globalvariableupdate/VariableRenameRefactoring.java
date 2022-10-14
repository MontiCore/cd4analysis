package de.monticore.coevolution.globalvariableupdate;

import spoon.refactoring.AbstractRenameRefactoring;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.visitor.chain.CtConsumer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Pattern;

public class VariableRenameRefactoring extends AbstractRenameRefactoring<CtVariable<?>> {
  private Collection<CtElement> scope;

  public VariableRenameRefactoring(Collection<CtElement> scope){
    super(Pattern.compile("[a-z]\\w*"));
    this.scope = new ArrayList<>(scope);
  }

  @Override
  protected void refactorNoCheck() {
    getTarget().map(new VariableReferenceFunction(scope)).
      forEach((CtConsumer<CtVariableAccess<?>>) t -> t.getVariable().setSimpleName(newName));
    getTarget().setSimpleName(newName);
  }
}
