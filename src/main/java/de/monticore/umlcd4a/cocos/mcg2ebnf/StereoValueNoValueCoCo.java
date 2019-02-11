/*
 * Copyright (c) 2017, MontiCore. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.umlcd4a.cocos.mcg2ebnf;

import de.monticore.umlcd4a.cd4analysis._ast.ASTCDStereoValue;
import de.monticore.umlcd4a.cd4analysis._cocos.CD4AnalysisASTCDStereoValueCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * StereoValues may only have a name but not a value.
 *
 * @author Robert Heim
 */
@Deprecated //new class at cd4analysis/src/main/java/de/monticore/cd
public class StereoValueNoValueCoCo implements CD4AnalysisASTCDStereoValueCoCo {
  
  /**
   * @see de.monticore.umlcd4a._cocos.CD4AnalysisASTCDStereoValueCoCo#check(de.monticore.umlcd4a._ast.ASTCDStereoValue)
   */
  @Override
  public void check(ASTCDStereoValue node) {
    if (node.isPresentValue()) {
      Log.error(String.format("0xC4A73 StereoValue %s may not have a value.", node.getName()),
          node.get_SourcePositionStart());
      
    }
  }
  
}
