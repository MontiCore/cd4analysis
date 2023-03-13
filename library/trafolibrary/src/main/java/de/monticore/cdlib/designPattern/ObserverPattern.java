/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdlib.designPattern;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdlib.designpatterns.observer.tf.Observer;
import de.se_rwth.commons.logging.Log;

/**
 * Introduce Observer Pattern
 *
 * <p>Created by
 *
 * @author hoelldobler, KE
 * @montitoolbox
 */
public class ObserverPattern implements DesignPattern {

  public ObserverPattern() {}

  /**
   * Applies the observer pattern to the the given class {@code subjectName}
   *
   * @param subjectName - name of the class a observer should be introduced for
   * @param observerName - name of the observer
   * @param observableName - name of the observable
   * @param ast - class diagram to be transformed
   * @return true, if applied successfully
   */
  public boolean introduceObserverPattern(
      String subjectName, String observerName, String observableName, ASTCDCompilationUnit ast) {

    String concreteObserverName = "Concrete" + observerName;

    // Create observer, observable and concreteObserver
    Observer observer = new Observer(ast);
    observer.set_$concreteObserver(concreteObserverName);
    observer.set_$observer(observerName);
    observer.set_$delete("delete" + observerName);
    observer.set_$add("add" + observerName);
    observer.set_$set("set" + observerName);
    observer.set_$notify("notify" + observerName);
    observer.set_$observable(observableName);
    observer.set_$subjectName(subjectName);
    if (observer.doPatternMatching()) {
      observer.doReplacement();

      if (transformationUtility.createInheritanceToClass(subjectName, observableName, ast)) {
        return true;
      } else {
        Log.info(
            "0xF4041: Could not introduce Inheritance between "
                + subjectName
                + " and "
                + observableName,
            ObserverPattern.class.getName());
      }
    }

    Log.info(
        "0xF4042: Could not introduce Design Pattern Observer", ObserverPattern.class.getName());
    return false;
  }
}
