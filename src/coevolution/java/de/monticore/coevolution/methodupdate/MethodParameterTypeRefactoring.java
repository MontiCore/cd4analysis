package de.monticore.coevolution.methodupdate;

import spoon.Launcher;
import spoon.refactoring.AbstractRenameRefactoring;
import spoon.reflect.declaration.*;
import spoon.reflect.factory.Factory;
import spoon.reflect.visitor.chain.CtConsumer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Pattern;

public class MethodParameterTypeRefactoring extends AbstractRenameRefactoring<CtParameter<?>> {
  private final Collection<CtElement> scope;
  private final CtClass<?> clas;

  public MethodParameterTypeRefactoring(Collection<CtElement> scope, CtClass<?> clas){
    super(Pattern.compile("[a-zA-Z]\\w*"));
    this.scope = new ArrayList<>(scope);
    this.clas = clas;
  }

  @Override
  protected void refactorNoCheck() {

    Launcher launcher = new Launcher();
    final Factory factory = launcher.getFactory();

    getTarget().map(new MethodParametersTypeFunktion(scope,clas)).
      forEach((CtConsumer<CtParameter<?>>) t -> t.setType(factory.createReference(newName)));
    getTarget().setType(factory.createReference(newName));
  }













}
