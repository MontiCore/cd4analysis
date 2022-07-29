package de.monticore.coevolution;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import spoon.Launcher;
import spoon.processing.Processor;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.visitor.Filter;
import spoon.reflect.visitor.filter.TypeFilter;


public class NameImpl<T> {

  /** the content of the Java source code file to be mutated */
  private String sourceCodeToBeUpdated;

  /** mutation operator */
  private Processor<CtElement> updater;

  /** the produced mutants */
  private final List<CtClass> updates = new ArrayList<>();


  public NameImpl(String src, Processor<CtElement> mutator) {
    this.sourceCodeToBeUpdated = src;
    this.updater = mutator;
  }

  /** returns a list of mutant classes */
  public String generateUpdates() {
    Launcher l = new Launcher();
    l.addInputResource(sourceCodeToBeUpdated);
    l.buildModel();

    CtClass origClass = (CtClass) l.getFactory().Package().getRootPackage()
      .getElements(new TypeFilter(CtClass.class)).get(0);
    System.out.println(origClass.getQualifiedName());

    List<CtElement> elementsToBeUpdated = origClass.getElements(new Filter<CtElement>() {

      @Override
      public boolean matches(CtElement arg0) {
        return updater.isToBeProcessed(arg0);
      }
    });

    for (CtElement e : elementsToBeUpdated) {
      updater.process(e);
    }

    CtClass klass = l.getFactory().Core().clone(origClass);

    String classContent = klass.toString();

    return classContent;

  }

  public List<CtClass> getUpdates() {
    return Collections.unmodifiableList(updates);
  }
}
