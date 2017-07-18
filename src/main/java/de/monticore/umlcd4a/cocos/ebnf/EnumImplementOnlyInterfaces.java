/*
 * Copyright (c) 2017 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.umlcd4a.cocos.ebnf;

import de.monticore.umlcd4a.cd4analysis._ast.ASTCDEnum;
import de.monticore.umlcd4a.cd4analysis._cocos.CD4AnalysisASTCDEnumCoCo;

/**
 * See {@link ImplementOnlyInterfaces}.
 *
 * @author Robert Heim
 */
public class EnumImplementOnlyInterfaces extends ImplementOnlyInterfaces implements
    CD4AnalysisASTCDEnumCoCo {
  
  /**
   * @see de.monticore.umlcd4a._cocos.CD4AnalysisASTCDEnumCoCo#check(de.monticore.umlcd4a.cd4analysis._ast.ASTCDEnum)
   */
  @Override
  public void check(ASTCDEnum node) {
    check("enum", node);
  }
}
