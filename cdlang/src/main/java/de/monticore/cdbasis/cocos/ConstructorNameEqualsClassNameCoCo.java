/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdbasis.cocos;

import de.monticore.cd4codebasis._ast.ASTCDConstructor;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._cocos.CDBasisASTCDClassCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Validates that all constructors of a CD class have the same name as the class.
 * 
 * A constructor is identified as a method without a return type (CDConstructor).
 * This CoCo ensures that generated Java code is valid, since Java requires
 * constructors to have the same name as their class.
 */
public class ConstructorNameEqualsClassNameCoCo implements CDBasisASTCDClassCoCo {

  public static final String ERROR_CODE = "0xCDC50";
  public static final String ERROR_MESSAGE = ERROR_CODE
      + ": Invalid constructor name '%s' in class '%s'. "
      + "Constructors must have the same name as their class.";

  @Override
  public void check(ASTCDClass node) {
    String className = node.getName();

    // Check all constructors in the class
    for (ASTCDConstructor constructor : node.getCDConstructorList()) {
      if (!constructor.getName().equals(className)) {
        Log.error(
            String.format(ERROR_MESSAGE, constructor.getName(), className),
            constructor.get_SourcePositionStart()
        );
      }
    }
  }
}
