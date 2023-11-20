/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdlib.designPattern;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdlib.designpatterns.decorator.tf.Decorator;
import de.se_rwth.commons.logging.Log;
import java.io.IOException;

/**
 * Introduce Decorator Pattern
 *
 * <p>Created by
 *
 * @author hoelldobler, KE
 * @montitoolbox
 */
public class DecoratorPattern implements DesignPattern {

  public DecoratorPattern() {}

  /**
   * Applies the decorator pattern to a class {@code concreteComponent} and a method {@code method}.
   *
   * @param concreteComponent - name of the concrete component
   * @param componentName - name of the decorator
   * @param method - the method
   * @param ast - class diagram to be transformed
   * @return true, if applied successfully
   */
  public boolean introduceDecoratorPattern(
      String concreteComponent, String componentName, String method, ASTCDCompilationUnit ast)
      throws IOException {
    // Set variables for transformation
    String decoratorName = concreteComponent + "Decorator";

    // Create Decorator class
    Decorator decorator = new Decorator(ast);
    decorator.set_$componentName(componentName);
    decorator.set_$decoratorName(decoratorName);
    decorator.set_$concreteComponent(concreteComponent);
    decorator.set_$MName(method);
    if (decorator.doPatternMatching()) {
      decorator.doReplacement();
      return true;
    }
    Log.info(
        "0xF4021: Could not introduce Design Pattern Object Adapter",
        DecoratorPattern.class.getName());
    return false;
  }
}
