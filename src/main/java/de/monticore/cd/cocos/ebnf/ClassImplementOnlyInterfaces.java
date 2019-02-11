/*
 * Copyright (c) 2017, MontiCore. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.cd.cocos.ebnf;

import de.monticore.cd.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._cocos.CD4AnalysisASTCDClassCoCo;

/**
 * See {@link ImplementOnlyInterfaces}.
 *
 * @author Robert Heim
 */
public class ClassImplementOnlyInterfaces extends ImplementOnlyInterfaces implements
    CD4AnalysisASTCDClassCoCo {

  /**
   * @see de.monticore.umlcd4a._cocos.CD4AnalysisASTCDClassCoCo#check(ASTCDClass)
   */
  @Override
  public void check(ASTCDClass node) {
    check("class", node);
  }
}
