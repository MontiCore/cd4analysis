/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.umlcd4a.cocos.mcg2ebnf;

import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDInterface;
import de.monticore.umlcd4a.cd4analysis._cocos.CD4AnalysisASTCDInterfaceCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Disables usage of attributes in interfaces.
 *
 * @author Robert Heim
 */
public class InterfaceNoAttributesCoCo implements CD4AnalysisASTCDInterfaceCoCo {
  
  /**
   * @see de.monticore.umlcd4a._cocos.CD4AnalysisASTCDInterfaceCoCo#check(de.monticore.umlcd4a.cd4analysis._ast.ASTCDInterface)
   */
  @Override
  public void check(ASTCDInterface node) {
    if (node.getCDAttributes().size() > 0) {
      ASTCDAttribute attr = node.getCDAttributes().get(0);
      Log.error(String.format("0xC4A66 Interface %s may not have attributes.", node.getName()),
          attr.get_SourcePositionStart());
    }
  }
  
}
