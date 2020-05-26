/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.cocos.mcg2ebnf;

import de.monticore.cd.cd4analysis._ast.ASTCDStereoValue;
import de.monticore.cd.cd4analysis._cocos.CD4AnalysisASTCDStereoValueCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * StereoValues may only have a name but not a value.
 *
 */
public class StereoValueNoValueCoCo implements CD4AnalysisASTCDStereoValueCoCo {
  
  /**
   * @see de.monticore.cd._cocos.CD4AnalysisASTCDStereoValueCoCo#check(de.monticore.cd._ast.ASTCDStereoValue)
   */
  @Override
  public void check(ASTCDStereoValue node) {
    if (node.isPresentValue()) {
      Log.error(String.format("0xC4A73 StereoValue %s may not have a value.", node.getName()),
          node.get_SourcePositionStart());
      
    }
  }
  
}
