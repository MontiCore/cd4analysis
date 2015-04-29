/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.umlcd4a.cocos.ebnf;

import java.util.Optional;

import de.monticore.cocos.CoCoLog;
import de.monticore.umlcd4a.BuiltInTypes;
import de.monticore.umlcd4a._ast.ASTCDAttribute;
import de.monticore.umlcd4a._cocos.CD4AnalysisASTCDAttributeCoCo;
import de.monticore.umlcd4a.symboltable.CDFieldSymbol;
import de.monticore.umlcd4a.symboltable.CDTypeSymbol;

/**
 * Checks that types of attributes are resolvable.
 *
 * @author Robert Heim
 */
public class AttributeTypeExists
    implements CD4AnalysisASTCDAttributeCoCo {
  
  public static final String ERROR_CODE = "0xC4A14";
  
  public static final String ERROR_MSG_FORMAT = "Type %s of the attribute %s is unknown.";
  
  /**
   * @see de.monticore.umlcd4a._cocos.CD4AnalysisASTCDAttributeCoCo#check(de.monticore.umlcd4a._ast.ASTCDAttribute)
   */
  @Override
  public void check(ASTCDAttribute node) {
    CDFieldSymbol attrSym = (CDFieldSymbol) node.getSymbol().get();
    String typeName = attrSym.getType().getName();
    if (!BuiltInTypes.isBuiltInType(typeName)) {
      Optional<CDTypeSymbol> subClassSym = node.getEnclosingScope().get()
          .resolve(typeName, CDTypeSymbol.KIND);
      if (!subClassSym.isPresent()) {
        CoCoLog.error(ERROR_CODE,
            String.format(ERROR_MSG_FORMAT,
                typeName,
                attrSym.getName()),
            node.get_SourcePositionStart());
      }
    }
  }
}
