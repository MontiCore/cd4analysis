/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.umlcd4a.cocos.mcg2ebnf;

import de.monticore.cocos.CoCoLog;
import de.monticore.umlcd4a._ast.ASTCDAttribute;
import de.monticore.umlcd4a._ast.ASTCDInterface;
import de.monticore.umlcd4a._cocos.CD4AnalysisASTCDInterfaceCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Disables usage of attributes in interfaces.
 *
 * @author Robert Heim
 */
public class InterfaceNoAttributesCoCo implements CD4AnalysisASTCDInterfaceCoCo {
  public static final String ERROR_CODE = "0xC4A66";
  
  public static final String ERROR_MSG_FORMAT = "Interface %s may not have attributes.";
  
  /**
   * @see de.monticore.umlcd4a._cocos.CD4AnalysisASTCDInterfaceCoCo#check(de.monticore.umlcd4a._ast.ASTCDInterface)
   */
  @Override
  public void check(ASTCDInterface node) {
    if (node.getCDAttributes().size() > 0) {
      ASTCDAttribute attr = node.getCDAttributes().get(0);
      CoCoLog.error(
          ERROR_CODE,
          String.format(ERROR_MSG_FORMAT, node.getName()),
          attr.get_SourcePositionStart());
    }
  }
  
}
