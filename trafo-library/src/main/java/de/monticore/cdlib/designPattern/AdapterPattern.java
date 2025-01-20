/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdlib.designPattern;

import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdlib.designpatterns.adapter.tf.ClassAdapter;
import de.monticore.cdlib.designpatterns.adapter.tf.ClassAdapterMethod;
import de.monticore.cdlib.designpatterns.adapter.tf.ObjectAdapter;
import de.monticore.cdlib.designpatterns.adapter.tf.ObjectAdapterMethod;
import de.monticore.cdlib.utilities.TransformationUtility;
import de.se_rwth.commons.logging.Log;

import java.io.IOException;

/**
 * Introduce Adapter Pattern
 *
 * <p>Created by
 *
 * @author hoelldobler, KE
 * @montitoolbox
 */
public class AdapterPattern implements DesignPattern {

  public AdapterPattern() {}

  /* Object adapter */

  /**
   * Applies the object adapter pattern to a class with name {@code adapteeName} without methods
   *
   * @param adapteeName - name of the adaptee class
   * @param targetName - name of the target
   * @param ast class diagram to be transformed
   * @return true, if applied successfully
   */
  public boolean introduceObjectAdapterPattern(
      String adapteeName, String targetName, ASTCDCompilationUnit ast) throws IOException {

    // Set variables for transformation
    String adapterName = adapteeName + "Adapter";
    ObjectAdapter adapter = new ObjectAdapter(ast);

    // Set Variables for Transformation
    adapter.set_$adapterName(adapterName);
    adapter.set_$targetName(targetName);
    adapter.set_$adapteeName(adapteeName);

    // do Transformation
    if (adapter.doPatternMatching()) {
      adapter.doReplacement();
      return true;
    }
    Log.info(
        "0xF4011: Could not introduce Design Pattern Object Adapter",
        AdapterPattern.class.getName());
    return false;
  }

  /**
   * Applies the object adapter pattern to a class with name {@code adapteeName} with methods
   *
   * @param adapteeName - name of the adaptee class
   * @param targetName - name of the target
   * @param method - the method
   * @param ast - class diagram to be transformed
   * @return true, if applied successfully
   */
  public boolean introduceObjectAdapterPattern(
      String adapteeName, String targetName, ASTCDMethod method, ASTCDCompilationUnit ast)
      throws IOException {

    String adapterName = adapteeName + "Adapter";
    ObjectAdapterMethod adapter = new ObjectAdapterMethod(ast);

    // Set Variables for Transformation
    adapter.set_$adapterName(adapterName);
    adapter.set_$targetName(targetName);
    adapter.set_$adapteeName(adapteeName);
    adapter.set_$A(method);
    adapter.set_$B(method.deepClone());

    // do Transformation
    if (adapter.doPatternMatching()) {
      adapter.doReplacement();
      return true;
    }
    Log.info(
        "0xF4012: Could not introduce Design Pattern Object Adapter",
        AdapterPattern.class.getName());
    return false;
  }

  /**
   * Applies the object adapter pattern to a class with name {@code adapteeName} with methods
   *
   * @param adapteeName - name of the adaptee class
   * @param targetName - name of the target
   * @param methodName - the method name
   * @param ast - class diagram to be transformed
   * @return true, if applied successfully
   */
  public boolean introduceObjectAdapterPattern(
    String adapteeName, String targetName, String methodName, ASTCDCompilationUnit ast)
    throws IOException {
    ASTCDMethod method = new TransformationUtility().getMethod(adapteeName, methodName, ast);
    return introduceObjectAdapterPattern(adapteeName, targetName, method, ast);
  }

  /* Class adapter with methods */

  /**
   * Applies the class adapter pattern to a class with name {@code adapteeName} with methods
   *
   * @param adapteeName - name of the adaptee class
   * @param targetName - name of the target
   * @param method - the method
   * @param ast - class diagram to be transformed
   * @return true, if applied successfully
   */
  public boolean introduceClassAdapterPattern(
      String adapteeName, String targetName, ASTCDMethod method, ASTCDCompilationUnit ast)
      throws IOException {
    // Set variables for transformation
    String adapterName = adapteeName + "Adapter";

    ClassAdapterMethod adapter = new ClassAdapterMethod(ast);
    adapter.set_$adapter(adapterName);
    adapter.set_$targetName(targetName);
    adapter.set_$adaptee(adapteeName);
    adapter.set_$A(method);
    adapter.set_$B(method.deepClone());

    if (adapter.doPatternMatching()) {
      adapter.doReplacement();
      return true;
    }
    Log.info(
        "0xF4013: Could not introduce Design Pattern Object Adapter",
        AdapterPattern.class.getName());
    return false;
  }

  /**
   * Applies the class adapter pattern to a class with name {@code adapteeName} with methods
   *
   * @param adapteeName - name of the adaptee class
   * @param targetName - name of the target
   * @param methodName - the method name
   * @param ast - class diagram to be transformed
   * @return true, if applied successfully
   */
  public boolean introduceClassAdapterPattern(
    String adapteeName, String targetName, String methodName, ASTCDCompilationUnit ast)
    throws IOException {
    ASTCDMethod method = new TransformationUtility().getMethod(adapteeName, methodName, ast);
    return introduceClassAdapterPattern(adapteeName, targetName, method, ast);
  }

  /* Class adapter without methods */

  /**
   * Applies the class adapter pattern to a class with name {@code adapteeName} without methods
   *
   * @param adapteeName - name of the adaptee class
   * @param targetName - name of the target
   * @param ast class diagram to be transformed
   * @return true, if applied successfully
   */
  public boolean introduceClassAdapterPattern(
      String adapteeName, String targetName, ASTCDCompilationUnit ast) throws IOException {
    // Set variables for transformation
    String adapterName = adapteeName + "Adapter";
    ClassAdapter adapter = new ClassAdapter(ast);
    adapter.set_$adapter(adapterName);
    adapter.set_$targetName(targetName);
    adapter.set_$adaptee(adapteeName);

    if (adapter.doPatternMatching()) {
      adapter.doReplacement();
      return true;
    }
    Log.info(
        "0xF4014: Could not introduce Design Pattern Object Adapter",
        AdapterPattern.class.getName());
    return false;
  }
}
