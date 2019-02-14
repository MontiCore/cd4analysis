/*
 * Copyright (c) 2017, MontiCore. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.cd.cocos.mcg2ebnf;

import de.monticore.cd.cd4analysis._ast.ASTCDAttribute;
import de.monticore.cd.cd4analysis._ast.ASTCDInterface;
import de.monticore.cd.cd4analysis._cocos.CD4AnalysisASTCDInterfaceCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Disables usage of attributes in interfaces.
 *
 * @author Robert Heim
 */
public class InterfaceNoAttributesCoCo implements CD4AnalysisASTCDInterfaceCoCo {
  
  /**
   * @see de.monticore.cd._cocos.CD4AnalysisASTCDInterfaceCoCo#check(ASTCDInterface)
   */
  @Override
  public void check(ASTCDInterface node) {
    if (!node.getCDAttributeList().isEmpty()) {
      ASTCDAttribute attr = node.getCDAttributeList().get(0);
      Log.error(String.format("0xC4A66 Interface %s may not have attributes.", node.getName()),
          attr.get_SourcePositionStart());
    }
  }
  
}
