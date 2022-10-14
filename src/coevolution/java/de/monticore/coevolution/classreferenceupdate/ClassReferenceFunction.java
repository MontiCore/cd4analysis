package de.monticore.coevolution.classreferenceupdate;

import spoon.reflect.code.CtConstructorCall;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtType;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtScanner;
import spoon.reflect.visitor.CtVisitor;
import spoon.reflect.visitor.chain.CtConsumableFunction;
import spoon.reflect.visitor.chain.CtConsumer;
import spoon.reflect.visitor.filter.DirectReferenceFilter;

import java.util.ArrayList;
import java.util.Collection;

public class ClassReferenceFunction implements CtConsumableFunction<CtElement> {

  protected CtConsumer<Object> outputConsumer;
  private CtType<?> type;
  private final CtVisitor visitor = new Visitor();
  private final Collection<CtElement> scope;

  public ClassReferenceFunction(Collection<CtElement> scope){
    this.scope = new ArrayList<>(scope);
  }

  @Override
  public void apply(CtElement element, CtConsumer<Object> outputConsumer) {
    if(!(element instanceof CtType)){
      throw new IllegalStateException("The input should be a typed element, e.g class reference.");
    }

    type = (CtType<?>) element;
    this.outputConsumer = outputConsumer;

    for (CtElement ctElement : scope) {
      ctElement.accept(visitor);
    }
  }

  protected class Visitor extends CtScanner {
    protected void maybeAccept(CtTypeReference<?> localType){
      if(new DirectReferenceFilter<>(ClassReferenceFunction.this.type.getReference()).matches(localType)){
        outputConsumer.accept(localType);
      }
    }

    @Override
    public <T> void visitCtField(CtField<T> f) {
      maybeAccept(f.getType());
      super.visitCtField(f);
    }

    @Override
    public <T> void visitCtConstructorCall(CtConstructorCall<T> ctConstructorCall) {
      maybeAccept(ctConstructorCall.getType());
      super.visitCtConstructorCall(ctConstructorCall);
    }

    @Override
    public <T> void visitCtTypeReference(CtTypeReference<T> reference) {
      maybeAccept(reference);
    }
  }

}
