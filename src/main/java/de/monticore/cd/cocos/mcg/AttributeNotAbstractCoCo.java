/*
 * Copyright (c) 2017, MontiCore. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.cd.cocos.mcg;

import de.monticore.cd.cd4analysis._ast.ASTCDAttribute;
import de.monticore.cd.cd4analysis._cocos.CD4AnalysisASTCDAttributeCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Attributes may not be abstract all others are allowed.
 *
 * @author Robert Heim
 */
public class AttributeNotAbstractCoCo implements CD4AnalysisASTCDAttributeCoCo {
  
  /**
   * @see de.monticore.cd._cocos.CD4AnalysisASTCDAttributeCoCo#check(ASTCDAttribute)
   */
  @Override
  public void check(ASTCDAttribute attr) {
    if (attr.isPresentModifier() && attr.getModifier().isAbstract()) {
      Log.error(String.format("0xC4A52 Attribute %s may not be abstract.", attr.getName()),
          attr.get_SourcePositionStart());
    }
  }
  
}
