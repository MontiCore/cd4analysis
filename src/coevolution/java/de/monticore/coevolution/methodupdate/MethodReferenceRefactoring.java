package de.monticore.coevolution.methodupdate;

import spoon.refactoring.AbstractRenameRefactoring;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.visitor.chain.CtConsumer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Pattern;

public class MethodReferenceRefactoring extends AbstractRenameRefactoring<CtMethod<?>> {

  private final Collection<CtElement> scope;
  private final CtClass<?> clas;

  public MethodReferenceRefactoring(Collection<CtElement> scope, CtClass<?> clas){
    super(Pattern.compile("[a-z]\\w*"));
    this.scope = new ArrayList<>(scope);
    this.clas = clas;
  }

  @Override
  protected void refactorNoCheck() {

    getTarget().map(new MethodReferenceFunction(scope,clas)).
      forEach((CtConsumer<CtInvocation<?>>) t -> t.getExecutable().setSimpleName(newName));
    getTarget().setSimpleName(newName);
  }
}
