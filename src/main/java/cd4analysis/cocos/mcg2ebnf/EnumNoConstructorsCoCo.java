/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package cd4analysis.cocos.mcg2ebnf;

import de.cd4analysis._ast.ASTCDConstructor;
import de.cd4analysis._ast.ASTCDEnum;
import de.cd4analysis._cocos.CD4AnalysisASTCDEnumCoCo;
import de.monticore.cocos.CoCoHelper;
import de.se_rwth.commons.logging.Log;

/**
 * Disables usage of constructors in classes.
 *
 * @author Robert Heim
 */
public class EnumNoConstructorsCoCo implements CD4AnalysisASTCDEnumCoCo {
  public static final String ERROR_CODE = "0xCD4AC3009";
  
  public static final String ERROR_MSG_FORMAT = "Enum %s may not have constructors.";
  
  /**
   * @see de.cd4analysis._cocos.CD4AnalysisASTCDEnumCoCo#check(de.cd4analysis._ast.ASTCDEnum)
   */
  @Override
  public void check(ASTCDEnum node) {
    if (node.getCDConstructors().size() > 0) {
      ASTCDConstructor constr = node.getCDConstructors().get(0);
      Log.error(CoCoHelper.buildErrorMsg(
          ERROR_CODE,
          String.format(ERROR_MSG_FORMAT, node.getName()),
          constr.get_SourcePositionStart()));
    }
  }
  
}
