package de.monticore.coevolution.methodupdate;

import spoon.Launcher;
import spoon.refactoring.AbstractRenameRefactoring;
import spoon.refactoring.RefactoringException;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.chain.CtConsumer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Pattern;

public class MethodReturnTypRefactoring extends AbstractRenameRefactoring<CtMethod<?>> {

  private final Collection<CtElement> scope;
  private final CtClass<?> clas;

  public MethodReturnTypRefactoring(Collection<CtElement> scope, CtClass<?> clas) {
    super(Pattern.compile("[a-zA-Z]\\w*"));
    this.scope = new ArrayList<>(scope);
    this.clas = clas;
  }

  @Override
  protected void refactorNoCheck() {

    Launcher launcher = new Launcher();
    final Factory factory = launcher.getFactory();

    getTarget().map(new MethodReturnTypFunction(scope, clas)).
      forEach((CtConsumer<CtInvocation<?>>) t
        -> t.getParent(CtVariable.class).setType(factory.createReference(newName)));

    getTarget().setType(factory.createReference(newName));
  }

  @Override
  protected void checkNewNameIsValid() {

    Launcher launcher = new Launcher();
    final Factory factory = launcher.getFactory();

    if(getTarget().map(new MethodReturnTypFunction(scope, clas)).list().size() > 0){
      CtInvocation<?> invocation = (CtInvocation<?>) getTarget().map(new MethodReturnTypFunction(scope, clas)).list().get(0);
      CtVariable<?> ctVariable = invocation.getParent(CtVariable.class);
      CtTypeReference<?> ctTypeReferenceVariable = ctVariable.getType();

      if(newName.equals("Object")){
        throw new RefactoringException(ctTypeReferenceVariable.getSimpleName()+" can not be updated as "+newName);
      }
    }
  }



}
