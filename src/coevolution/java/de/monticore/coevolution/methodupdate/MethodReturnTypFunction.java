package de.monticore.coevolution.methodupdate;

import spoon.reflect.code.CtInvocation;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.visitor.CtScanner;
import spoon.reflect.visitor.CtVisitor;
import spoon.reflect.visitor.chain.CtConsumableFunction;
import spoon.reflect.visitor.chain.CtConsumer;

import java.util.ArrayList;
import java.util.Collection;

public class MethodReturnTypFunction implements CtConsumableFunction<CtElement> {

  protected CtConsumer<Object> outputConsumer;
  private CtMethod<?> method;

  private final CtClass<?> clas;
  private final CtVisitor visitor = new MethodReturnTypFunction.Visitor();
  private final Collection<CtElement> scope;

  public MethodReturnTypFunction(Collection<CtElement> scope, CtClass<?> clas){
    this.clas = clas;
    this.scope = new ArrayList<>(scope);
  }

  @Override
  public void apply(CtElement element, CtConsumer<Object> outputConsumer) {
    if(!(element instanceof CtMethod)){
      throw new IllegalStateException("The Input should be a method reference.");
    }

    method = (CtMethod<?>) element;
    this.outputConsumer = outputConsumer;

    for (CtElement ctElement : scope) {
      ctElement.accept(visitor);
    }
  }

  protected class Visitor extends CtScanner {
    protected void maybeAccept(CtInvocation<?> method){
      if(MethodReturnTypFunction.this.method.getSimpleName().equals(method.getExecutable().getSimpleName()) &&
        clas.getSimpleName().equals(method.getTarget().getType().getSimpleName()) &&
      method.getParent(CtVariable.class) != null){
        outputConsumer.accept(method);
      }
    }

    @Override
    public <T> void visitCtInvocation(CtInvocation<T> method) {
      maybeAccept(method);
      super.visitCtInvocation(method);
    }
  }
}
