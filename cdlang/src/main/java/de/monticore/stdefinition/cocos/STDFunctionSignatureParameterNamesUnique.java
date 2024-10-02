/* (c) https://github.com/MontiCore/monticore */
package de.monticore.stdefinition.cocos;

import de.monticore.cd.cocos.CoCoHelper;
import de.monticore.cd4codebasis._ast.ASTCDParameter;
import de.monticore.stdefinition._ast.ASTSTDFunction;
import de.monticore.stdefinition._cocos.STDefinitionASTSTDFunctionCoCo;
import de.se_rwth.commons.logging.Log;
import java.util.List;

public class STDFunctionSignatureParameterNamesUnique implements STDefinitionASTSTDFunctionCoCo {

  @Override
  public void check(ASTSTDFunction node) {
    List<ASTCDParameter> dups =
        CoCoHelper.findDuplicatesBy(node.getCDParameterList(), ASTCDParameter::getName);
    for (ASTCDParameter dup : dups) {
      Log.error(
          "0xFDC90 name \""
              + dup.getName()
              + "\""
              + " used multiple times for parameters in the same signature.",
          node.get_SourcePositionStart(),
          node.get_SourcePositionEnd());
    }
  }
}
