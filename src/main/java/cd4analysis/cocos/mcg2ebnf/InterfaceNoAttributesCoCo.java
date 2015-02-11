/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package cd4analysis.cocos.mcg2ebnf;

import de.cd4analysis._ast.ASTCDAttribute;
import de.cd4analysis._ast.ASTCDInterface;
import de.cd4analysis._cocos.CD4AnalysisASTCDInterfaceCoCo;
import de.monticore.cocos.CoCoHelper;
import de.se_rwth.commons.logging.Log;

/**
 * Disables usage of attributes in interfaces.
 *
 * @author Robert Heim
 */
public class InterfaceNoAttributesCoCo implements CD4AnalysisASTCDInterfaceCoCo {
  public static final String ERROR_CODE = "0xCD4AC3006";
  
  public static final String ERROR_MSG_FORMAT = "Interface %s may not have attributes.";
  
  /**
   * @see de.cd4analysis._cocos.CD4AnalysisASTCDInterfaceCoCo#check(de.cd4analysis._ast.ASTCDInterface)
   */
  @Override
  public void check(ASTCDInterface node) {
    if (node.getCDAttributes().size() > 0) {
      ASTCDAttribute attr = node.getCDAttributes().get(0);
      Log.error(CoCoHelper.buildErrorMsg(
          ERROR_CODE,
          String.format(ERROR_MSG_FORMAT, node.getName()),
          attr.get_SourcePositionStart()));
    }
  }
  
}
