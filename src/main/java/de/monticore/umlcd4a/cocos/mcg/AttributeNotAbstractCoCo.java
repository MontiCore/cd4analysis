/*
 * Copyright (c) 2017 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.umlcd4a.cocos.mcg;

import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._cocos.CD4AnalysisASTCDAttributeCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Attributes may not be abstract all others are allowed.
 *
 * @author Robert Heim
 */
public class AttributeNotAbstractCoCo implements CD4AnalysisASTCDAttributeCoCo {
  
  /**
   * @see de.monticore.umlcd4a._cocos.CD4AnalysisASTCDAttributeCoCo#check(de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute)
   */
  @Override
  public void check(ASTCDAttribute attr) {
    if (attr.getModifier().isPresent() && attr.getModifier().get().isAbstract()) {
      Log.error(String.format("0xC4A52 Attribute %s may not be abstract.", attr.getName()),
          attr.get_SourcePositionStart());
    }
  }
  
}
