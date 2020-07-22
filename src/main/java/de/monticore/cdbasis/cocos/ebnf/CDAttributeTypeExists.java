/*
 * (c) https://github.com/MontiCore/monticore
 */
package de.monticore.cdbasis.cocos.ebnf;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._cocos.CDBasisASTCDAttributeCoCo;

/**
 * Checks that types of attributes are resolvable.
 */
public class CDAttributeTypeExists
    implements CDBasisASTCDAttributeCoCo {

  @Override
  public void check(ASTCDAttribute node) {
    node.getSymbol().getType().getTypeInfo();
    // the type is automatically resolved
    /*
    if (!node.getEnclosingScope().resolveType(typeName).isPresent()) {
      Log.error(String.format("0xCDC05: Type %s of the attribute %s is unknown.",
          typeName,
          node.getName()),
          node.get_SourcePositionStart());
    }
     */
  }
}
