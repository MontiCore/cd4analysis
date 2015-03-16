/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.umlcd4a.cocos.mcg;

import de.cd4analysis._ast.ASTCDAttribute;
import de.cd4analysis._ast.ASTCDInterface;
import de.cd4analysis._cocos.CD4AnalysisASTCDInterfaceCoCo;
import de.monticore.cocos.CoCoHelper;
import de.se_rwth.commons.logging.Log;

/**
 * Attributes in interfaces must be static.
 *
 * @author Robert Heim
 */
public class InterfaceAttributesStaticCoCo implements CD4AnalysisASTCDInterfaceCoCo {
  public static final String ERROR_CODE = "0xC4A51";
  
  public static final String ERROR_MSG_FORMAT = "Attribute %s in interface %s must be static.";
  
  /**
   * @see de.monticore.umlcd4a._cocos.CD4AnalysisASTCDInterfaceCoCo#check(de.monticore.umlcd4a._ast.ASTCDInterface)
   */
  @Override
  public void check(ASTCDInterface node) {
    for (ASTCDAttribute attr : node.getCDAttributes()) {
      if (!attr.getModifier().isPresent() || !attr.getModifier().get().isStatic()) {
        Log.error(CoCoHelper.buildErrorMsg(
            ERROR_CODE,
            String.format(ERROR_MSG_FORMAT, attr.getName(), node.getName()),
            attr.get_SourcePositionStart()));
      }
    }
  }
  
}
