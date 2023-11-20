/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdlib.designPattern;

import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdlib.designpatterns.proxy.tf.Proxy;
import de.se_rwth.commons.logging.Log;
import java.util.List;

/**
 * Introduce Proxy Pattern
 *
 * <p>Created by
 *
 * @author hoelldobler, KE
 * @montitoolbox
 */
public class ProxyPattern implements DesignPattern {

  public ProxyPattern() {}

  /**
   * Applies the proxy pattern to a given class {@code className}
   *
   * @param className - name of the class
   * @param ast - class diagram to be transformed
   * @return true, if applied successfully
   */
  public boolean introduceProxyPattern(String className, ASTCDCompilationUnit ast) {

    if (!createProxy(className, ast)) {
      return false;
    }
    return true;
  }

  /**
   * Applies the proxy pattern to a given class {@code className} with several methods {@code
   * methods}
   *
   * @param className - name of the class
   * @param methods - list of methods
   * @param ast - class diagram to be transformed
   * @return true, if applied successfully
   */
  public boolean introduceProxyPattern(
      String className, List<String> methods, ASTCDCompilationUnit ast) {

    String interfaceName = "I" + className;
    String proxyName = "Proxy" + className;
    // Create Proxy Class
    if (!createProxy(className, ast)) {
      return false;
    }

    // Add Methods from className to Proxy and interface
    for (int i = 0; i < methods.size(); i++) {
      ASTCDMethod m = transformationUtility.getMethod(methods.get(i), className, ast);
      transformationUtility.addMethodToInterface(m.deepClone(), interfaceName, ast);
      transformationUtility.addMethod(m.deepClone(), proxyName, ast);
    }
    return true;
  }

  // Introduce Proxy Pattern
  private boolean createProxy(String className, ASTCDCompilationUnit ast) {

    String interfaceName = "I" + className;
    String proxyName = "Proxy" + className;

    // Create proxy and interface and the association between proxy and
    // class
    Proxy proxy = new Proxy(ast);
    proxy.set_$interfaceName(interfaceName);
    proxy.set_$proxyName(proxyName);
    proxy.set_$realClassName(className);
    if (proxy.doPatternMatching()) {
      proxy.doReplacement();
    } else {
      Log.info("0xF4051: Could not introduce Design Pattern Proxy", ProxyPattern.class.getName());
      return false;
    }

    // Create inheritance between class and interface
    if (!transformationUtility.addInheritanceToInterface(className, interfaceName, ast)) {
      Log.info(
          "0xF4052: Could not introduce Inheritance between Interface "
              + interfaceName
              + " and Class "
              + className,
          ProxyPattern.class.getName());
      return false;
    }
    return true;
  }
}
