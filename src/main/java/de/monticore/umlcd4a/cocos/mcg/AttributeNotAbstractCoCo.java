/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.umlcd4a.cocos.mcg;

import de.cd4analysis._ast.ASTCDAttribute;
import de.cd4analysis._cocos.CD4AnalysisASTCDAttributeCoCo;
import de.monticore.cocos.CoCoHelper;
import de.se_rwth.commons.logging.Log;

/**
 * Attributes may not be abstract all others are allowed.
 *
 * @author Robert Heim
 */
public class AttributeNotAbstractCoCo implements CD4AnalysisASTCDAttributeCoCo {
  public static final String ERROR_CODE = "0xC4A52";
  
  public static final String ERROR_MSG_FORMAT = "Attribute %s may not be abstract.";
  
  /**
   * @see de.monticore.umlcd4a._cocos.CD4AnalysisASTCDAttributeCoCo#check(de.monticore.umlcd4a._ast.ASTCDAttribute)
   */
  @Override
  public void check(ASTCDAttribute attr) {
    if (attr.getModifier().isPresent() && attr.getModifier().get().isAbstract()) {
      Log.error(CoCoHelper.buildErrorMsg(
          ERROR_CODE,
          String.format(ERROR_MSG_FORMAT, attr.getName()),
          attr.get_SourcePositionStart()));
    }
  }
  
}
