/*
 * Copyright (c) 2017, MontiCore. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.umlcd4a.cocos.mcg2ebnf;

import com.google.common.base.CharMatcher;

import de.monticore.umlcd4a.cd4analysis._ast.ASTCDEnum;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDEnumConstant;
import de.monticore.umlcd4a.cd4analysis._cocos.CD4AnalysisASTCDEnumCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Enums may not have modifiers.
 *
 * @author Robert Heim
 */
public class EnumConstantNameUpperCase implements CD4AnalysisASTCDEnumCoCo {
  
  /**
   * @see de.monticore.umlcd4a._cocos.CD4AnalysisASTCDEnumCoCo#check(de.monticore.umlcd4a.cd4analysis._ast.ASTCDEnum)
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
