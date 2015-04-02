/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.umlcd4a.cocos.mcg2ebnf;

import de.monticore.cocos.CoCoLog;
import de.monticore.umlcd4a._ast.ASTCDEnum;
import de.monticore.umlcd4a._ast.ASTCDMethod;
import de.monticore.umlcd4a._cocos.CD4AnalysisASTCDEnumCoCo;

/**
 * Disables usage of methods in enums.
 *
 * @author Robert Heim
 */
public class EnumNoMethodsCoCo implements CD4AnalysisASTCDEnumCoCo {
  public static final String ERROR_CODE = "0xC4A70";
  
  public static final String ERROR_MSG_FORMAT = "Enum %s may not have methods.";
  
  /**
   * @see de.monticore.umlcd4a._cocos.CD4AnalysisASTCDEnumCoCo#check(de.monticore.umlcd4a._ast.ASTCDEnum)
   */
  @Override
  public void check(ASTCDEnum node) {
    if (node.getCDMethods().size() > 0) {
      ASTCDMethod method = node.getCDMethods().get(0);
      CoCoLog.error(
          ERROR_CODE,
          String.format(ERROR_MSG_FORMAT, node.getName()),
          method.get_SourcePositionStart());
    }
  }
  
}
