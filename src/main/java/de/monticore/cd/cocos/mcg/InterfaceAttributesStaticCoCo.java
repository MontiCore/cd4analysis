/*
 * Copyright (c) 2017, MontiCore. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.cd.cocos.mcg;

import de.monticore.cd.cd4analysis._ast.ASTCDAttribute;
import de.monticore.cd.cd4analysis._ast.ASTCDInterface;
import de.monticore.cd.cd4analysis._cocos.CD4AnalysisASTCDInterfaceCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Attributes in interfaces must be static.
 *
 * @author Robert Heim
 */
public class InterfaceAttributesStaticCoCo implements CD4AnalysisASTCDInterfaceCoCo {
  
  /**
   * @see de.monticore.cd._cocos.CD4AnalysisASTCDInterfaceCoCo#check(ASTCDInterface)
   */
  @Override
  public void check(ASTCDInterface node) {
    for (ASTCDAttribute attr : node.getCDAttributeList()) {
      if (!attr.isPresentModifier() || !attr.getModifier().isStatic()) {
        Log.error(String.format("0xC4A51 Attribute %s in interface %s must be static.",
            attr.getName(), node.getName()),
            attr.get_SourcePositionStart());
      }
    }
  }
  
}
