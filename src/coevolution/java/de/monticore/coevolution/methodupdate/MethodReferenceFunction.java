package de.monticore.coevolution.methodupdate;

import spoon.reflect.code.CtInvocation;
import spoon.reflect.declaration.*;
import spoon.reflect.visitor.CtScanner;
import spoon.reflect.visitor.CtVisitor;
import spoon.reflect.visitor.chain.CtConsumableFunction;
import spoon.reflect.visitor.chain.CtConsumer;

import java.util.ArrayList;
import java.util.Collection;

public class MethodReferenceFunction implements CtConsumableFunction<CtElement> {

  protected CtConsumer<Object> outputConsumer;
  private CtMethod<?> method;
  private final CtClass<?> clas;
  private final CtVisitor visitor = new MethodReferenceFunction.Visitor();
  private final Collection<CtElement> scope;

  public MethodReferenceFunction(Collection<CtElement> scope, CtClass<?> clas){
    this.clas = clas;
    this.scope = new ArrayList<>(scope);
  }

  @Override
  public void apply(CtElement element, CtConsumer<Object> outputConsumer) {
    if(!(element instanceof CtMethod)){
      throw new IllegalStateException("The input should be a meethod reference.");
    }

    method = (CtMethod<?>) element;
    this.outputConsumer = outputConsumer;

    for (CtElement ctElement : scope) {
      ctElement.accept(visitor);
    }
  }

  protected class Visitor extends CtScanner {
    protected void maybeAccept(CtInvocation<?> method){
      if(MethodReferenceFunction.this.method.getSimpleName().equals(method.getExecutable().getSimpleName()) &&
        clas.getSimpleName().equals(method.getTarget().getType().getSimpleName())){
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
