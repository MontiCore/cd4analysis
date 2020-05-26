/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.cocos.mcg2ebnf;

import com.google.common.base.CharMatcher;
import de.monticore.cd.cd4analysis._ast.ASTCDEnum;
import de.monticore.cd.cd4analysis._ast.ASTCDEnumConstant;
import de.monticore.cd.cd4analysis._cocos.CD4AnalysisASTCDEnumCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Enums may not have modifiers.
 *
 */
public class EnumConstantNameUpperCase implements CD4AnalysisASTCDEnumCoCo {
  
  /**
   * @see de.monticore.cd._cocos.CD4AnalysisASTCDEnumCoCo#check(ASTCDEnum)
   */
  @Override
  public void check(ASTCDEnum node) {
    for (ASTCDEnumConstant a: node.getCDEnumConstantList()){
	  if (!CharMatcher.JAVA_UPPER_CASE.matchesAllOf(a.toString().replaceAll("_",""))) {
        Log.error(String.format("0xC4A73 Enum %s may only have lower case constants.", node.getName()),
          a.get_SourcePositionStart());
        break;
	  }  
	}      
  }
}
