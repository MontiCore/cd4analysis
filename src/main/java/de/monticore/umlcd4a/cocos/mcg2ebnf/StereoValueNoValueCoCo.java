/*
 * Copyright (c) 2017, MontiCore. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.umlcd4a.cocos.mcg2ebnf;

import de.monticore.umlcd4a.cd4analysis._ast.ASTStereoValue;
import de.monticore.umlcd4a.cd4analysis._cocos.CD4AnalysisASTStereoValueCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * StereoValues may only have a name but not a value.
 *
 * @author Robert Heim
 */
public class StereoValueNoValueCoCo implements CD4AnalysisASTStereoValueCoCo {
  
  /**
   * @see de.monticore.umlcd4a._cocos.CD4AnalysisASTStereoValueCoCo#check(de.monticore.umlcd4a._ast.ASTStereoValue)
   */
  @Override
  public void check(ASTStereoValue node) {
    if (node.isPresentValue()) {
      Log.error(String.format("0xC4A73 StereoValue %s may not have a value.", node.getName()),
          node.get_SourcePositionStart());
      
    }
  }
  
}
