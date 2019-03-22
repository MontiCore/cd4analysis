/*
 * Copyright (c) 2017, MontiCore. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.umlcd4a.cocos.ebnf;

import java.util.Optional;

import de.monticore.umlcd4a.BuiltInTypes;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._cocos.CD4AnalysisASTCDAttributeCoCo;
import de.monticore.umlcd4a.symboltable.CDFieldSymbol;
import de.monticore.umlcd4a.symboltable.CDTypeSymbol;
import de.se_rwth.commons.logging.Log;

/**
 * Checks that types of attributes are resolvable.
 *
 * @author Robert Heim
 */
public class AttributeTypeExists
    implements CD4AnalysisASTCDAttributeCoCo {
  
  /**
   * @see de.monticore.umlcd4a.cd4analysis._cocos.CD4AnalysisASTCDAttributeCoCo#check(de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute)
   */
  @Override
  public void check(ASTCDAttribute node) {
    CDFieldSymbol attrSym = (CDFieldSymbol) node.getSymbol();
    String typeName = attrSym.getType().getName();
    if (!BuiltInTypes.isBuiltInType(typeName)) {
      Optional<CDTypeSymbol> subClassSym = node.getEnclosingScope()
          .resolve(typeName, CDTypeSymbol.KIND);
      if (!subClassSym.isPresent()) {
        Log.error(String.format("0xC4A14 Type %s of the attribute %s is unknown.",
            typeName,
            attrSym.getName()),
            node.get_SourcePositionStart());
      }
    }
  }
}
