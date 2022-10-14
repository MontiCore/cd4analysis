package de.monticore.coevolution.localvariableupdate;

import spoon.refactoring.AbstractRenameRefactoring;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.visitor.chain.CtConsumer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Pattern;

public class LocalVariableRenameRefactoring extends AbstractRenameRefactoring<CtLocalVariable<?>> {
  private final Collection<CtElement> scope;

  public LocalVariableRenameRefactoring(Collection<CtElement> scope){
    super(Pattern.compile("[a-z]\\w*"));
    this.scope = new ArrayList<>(scope);
  }

  @Override
  protected void refactorNoCheck() {
    getTarget().map(new LocalVariableReferenceFunction(scope)).
      forEach((CtConsumer<CtVariableAccess<?>>) t -> t.getVariable().setSimpleName(newName));
    getTarget().setSimpleName(newName);
  }
}
