/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.umlcd4a.cocos.mcg2ebnf;

import de.monticore.cocos.CoCoLog;
import de.monticore.umlcd4a._ast.ASTStereoValue;
import de.monticore.umlcd4a._cocos.CD4AnalysisASTStereoValueCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * StereoValues may only have a name but not a value.
 *
 * @author Robert Heim
 */
public class StereoValueNoValueCoCo implements CD4AnalysisASTStereoValueCoCo {
  public static final String ERROR_CODE = "0xC4A73";
  
  public static final String ERROR_MSG_FORMAT = "StereoValue %s may not have a value.";
  
  /**
   * @see de.monticore.umlcd4a._cocos.CD4AnalysisASTStereoValueCoCo#check(de.monticore.umlcd4a._ast.ASTStereoValue)
   */
  @Override
  public void check(ASTStereoValue node) {
    if (node.getValue().isPresent()) {
      CoCoLog.error(
          ERROR_CODE,
          String.format(ERROR_MSG_FORMAT, node.getName()),
          node.get_SourcePositionStart());
      
    }
  }
  
}
