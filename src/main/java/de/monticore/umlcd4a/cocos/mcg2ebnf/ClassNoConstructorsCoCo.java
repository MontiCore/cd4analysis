/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.umlcd4a.cocos.mcg2ebnf;

import de.monticore.cocos.CoCoLog;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDConstructor;
import de.monticore.umlcd4a.cd4analysis._cocos.CD4AnalysisASTCDClassCoCo;

/**
 * Disables usage of constructors in classes.
 *
 * @author Robert Heim
 */
public class ClassNoConstructorsCoCo implements CD4AnalysisASTCDClassCoCo {
  public static final String ERROR_CODE = "0xC4A62";
  
  public static final String ERROR_MSG_FORMAT = "Class %s may not have constructors.";
  
  /**
   * @see de.monticore.umlcd4a._cocos.CD4AnalysisASTCDClassCoCo#check(de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass)
   */
  @Override
  public void check(ASTCDClass node) {
    if (node.getCDConstructors().size() > 0) {
      ASTCDConstructor constr = node.getCDConstructors().get(0);
      CoCoLog.error(
          ERROR_CODE,
          String.format(ERROR_MSG_FORMAT, node.getName()),
          constr.get_SourcePositionStart());
    }
  }
  
}
