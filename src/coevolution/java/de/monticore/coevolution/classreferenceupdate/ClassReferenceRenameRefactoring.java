package de.monticore.coevolution.classreferenceupdate;

import spoon.refactoring.AbstractRenameRefactoring;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtType;
import spoon.reflect.reference.CtReference;
import spoon.reflect.visitor.chain.CtConsumer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Pattern;

public class ClassReferenceRenameRefactoring extends AbstractRenameRefactoring<CtType<?>> {

  private final Collection<CtElement> scope;

  public ClassReferenceRenameRefactoring(Collection<CtElement> scope){
    super(Pattern.compile("[A-Z]\\w*"));
    this.scope = new ArrayList<>(scope);
  }

  @Override
  protected void refactorNoCheck() {
    getTarget().map(new ClassReferenceFunction(scope)).forEach((CtConsumer<CtReference>) t -> t.setSimpleName(newName));
    getTarget().setSimpleName(newName);
  }





}
