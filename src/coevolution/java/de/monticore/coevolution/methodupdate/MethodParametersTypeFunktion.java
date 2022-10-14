package de.monticore.coevolution.methodupdate;

import spoon.reflect.code.CtInvocation;
import spoon.reflect.declaration.*;
import spoon.reflect.visitor.CtScanner;
import spoon.reflect.visitor.CtVisitor;
import spoon.reflect.visitor.chain.CtConsumableFunction;
import spoon.reflect.visitor.chain.CtConsumer;

import java.util.ArrayList;
import java.util.Collection;

public class MethodParametersTypeFunktion implements CtConsumableFunction<CtElement> {

  protected CtConsumer<Object> outputConsumer;
  private CtParameter<?> parameter;
  private final CtVisitor visitor = new MethodParametersTypeFunktion.Visitor();
  private final Collection<CtElement> scope;

  public MethodParametersTypeFunktion(Collection<CtElement> scope, CtClass<?> clas){
    this.scope = new ArrayList<>(scope);
  }

  @Override
  public void apply(CtElement element, CtConsumer<Object> outputConsumer) {
    if(!(element instanceof CtParameter)){
      throw new IllegalStateException("The input should be a parameter of a method.");
    }

    parameter = (CtParameter<?>)  element;
    this.outputConsumer = outputConsumer;

    for (CtElement ctElement : scope) {
      ctElement.accept(visitor);
    }
  }

  protected class Visitor extends CtScanner {
    protected void maybeAccept(CtParameter<?> parameter) {

      int index = MethodParametersTypeFunktion.this.parameter.getParent(CtMethod.class).
        getParameters().indexOf(MethodParametersTypeFunktion.this.parameter);

      if (parameter.getParent(CtMethod.class) != null) {

        CtInvocation<?> selectedInvocation = parameter.getParent(CtMethod.class).
          filterChildren((CtInvocation<?> invocation) ->
            invocation.getExecutable().getSimpleName().
              matches(MethodParametersTypeFunktion.this.parameter.getParent(CtMethod.class).getSimpleName()) &&
              invocation.getTarget().getType().getSimpleName().
                matches(MethodParametersTypeFunktion.this.parameter.getParent(CtClass.class).getSimpleName())).first();

        if (selectedInvocation != null &&
          parameter.getSimpleName().matches(selectedInvocation.getArguments().get(index).toString())) {

          outputConsumer.accept(parameter);
          MethodParametersTypeFunktion.this.parameter = parameter;
          maybeAccept(parameter);

        }
      }
    }

    @Override
    public <T> void visitCtParameter(CtParameter<T> parameter) {
      maybeAccept(parameter);
      super.visitCtParameter(parameter);
    }
  }
}
