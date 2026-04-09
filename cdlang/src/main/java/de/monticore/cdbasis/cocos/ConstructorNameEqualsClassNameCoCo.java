/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdbasis.cocos;

import de.monticore.cd4codebasis._ast.ASTCDConstructor;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._cocos.CDBasisASTCDClassCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Validates that all constructors of a CD class have the same name as the class.
 */
public class ConstructorNameEqualsClassNameCoCo implements CDBasisASTCDClassCoCo {

  public static final String ERROR_CODE = "0xCDC50";
  public static final String ERROR_MESSAGE = ERROR_CODE
      + ": Invalid constructor name '%s' in class '%s'. "
      + "Constructors must have the same name as their class.";

  @Override
  public void check(ASTCDClass node) {
    String className = node.getName();

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
