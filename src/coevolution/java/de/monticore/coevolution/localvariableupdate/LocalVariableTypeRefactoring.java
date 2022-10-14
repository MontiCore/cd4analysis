package de.monticore.coevolution.localvariableupdate;

import spoon.Launcher;
import spoon.refactoring.AbstractRenameRefactoring;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.factory.Factory;
import spoon.reflect.visitor.chain.CtConsumer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Pattern;

public class LocalVariableTypeRefactoring extends AbstractRenameRefactoring<CtVariable<?>> {
  private final Collection<CtElement> scope;

  public LocalVariableTypeRefactoring(Collection<CtElement> scope){
    super(Pattern.compile("[a-zA-Z]\\w*"));
    this.scope = new ArrayList<>(scope);
  }

  @Override
  protected void refactorNoCheck() {
    Launcher launcher = new Launcher();
    final Factory factory = launcher.getFactory();

    getTarget().map(new LocalVariableTypeFunction(scope)).
      forEach((CtConsumer<CtVariableAccess<?>>) t -> t.setType(factory.createReference(newName)));
    getTarget().setType(factory.createReference(newName));
  }









}
