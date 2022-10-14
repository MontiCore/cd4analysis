package de.monticore.coevolution.globalvariableupdate;

import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.code.CtVariableRead;
import spoon.reflect.code.CtVariableWrite;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.visitor.CtScanner;
import spoon.reflect.visitor.CtVisitor;
import spoon.reflect.visitor.chain.CtConsumableFunction;
import spoon.reflect.visitor.chain.CtConsumer;
import spoon.reflect.visitor.filter.VariableAccessFilter;

import java.util.ArrayList;
import java.util.Collection;

public class VariableReferenceFunction implements CtConsumableFunction<CtElement> {
  protected CtConsumer<Object> outputConsumer;
  private CtVariable<?> variable;
  private final CtVisitor visitor = new VariableReferenceFunction.Visitor();
  private final Collection<CtElement> scope;

  public VariableReferenceFunction(Collection<CtElement> scope){
    this.scope = new ArrayList<>(scope);
  }

  @Override
  public void apply(CtElement element, CtConsumer<Object> outputConsumer) {
    if(!(element instanceof CtVariable)){
      throw new IllegalStateException("The input should be a global variable.");
    }

    variable = (CtVariable<?>) element;
    this.outputConsumer = outputConsumer;

    for (CtElement ctElement : scope) {
      ctElement.accept(visitor);
    }
  }

  protected class Visitor extends CtScanner {
    protected void maybeAccept(CtVariableAccess<?> parameter){
      if(new VariableAccessFilter<>(VariableReferenceFunction.this.variable.getReference()).
        matches(parameter)){
        outputConsumer.accept(parameter);
      }
    }

    @Override
    public <T> void visitCtVariableRead(CtVariableRead<T> variableRead) {
      maybeAccept(variableRead);
      super.visitCtVariableRead(variableRead);
    }

    @Override
    public <T> void visitCtVariableWrite(CtVariableWrite<T> variableWrite) {
      maybeAccept(variableWrite);
      super.visitCtVariableWrite(variableWrite);
    }






  }







}
