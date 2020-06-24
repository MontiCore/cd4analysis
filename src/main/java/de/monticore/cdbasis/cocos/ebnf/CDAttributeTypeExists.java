/*
 * (c) https://github.com/MontiCore/monticore
 */

/*
 * (c) https://github.com/MontiCore/monticore
 */

/*
 * (c) https://github.com/MontiCore/monticore
 */

/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdbasis.cocos.ebnf;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._cocos.CDBasisASTCDAttributeCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Checks that types of attributes are resolvable.
 */
public class CDAttributeTypeExists
    implements CDBasisASTCDAttributeCoCo {

  @Override
  public void check(ASTCDAttribute node) {
    final String typeName = node.getSymbol().getType().getTypeInfo().getName();
    if (!node.getEnclosingScope().resolveType(typeName).isPresent()) {
      Log.error(String.format("0xCDC05: Type %s of the attribute %s is unknown.",
          typeName,
          node.getName()),
          node.get_SourcePositionStart());
    }
  }
}
